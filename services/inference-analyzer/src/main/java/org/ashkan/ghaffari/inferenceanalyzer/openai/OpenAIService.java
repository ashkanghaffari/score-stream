package org.ashkan.ghaffari.inferenceanalyzer.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.ruleengine.RuleResult;
import org.ashkan.ghaffari.inferenceanalyzer.ChatTurn;
import org.ashkan.ghaffari.inferenceanalyzer.ConversationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OpenAIService {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";
    private static final ChatCompletionRequest.ResponseFormat JSON_OBJECT_FORMAT =
        new ChatCompletionRequest.ResponseFormat("json_object");

    private final WebClient webClient;
    private final OpenAIProperties properties;
    private final ObjectMapper mapper;

    public OpenAIService(WebClient webClient, OpenAIProperties properties,
                         com.fasterxml.jackson.databind.ObjectMapper mapper) {
        this.webClient = webClient;
        this.properties = properties;
        this.mapper = mapper;
    }

    public List<OpenAIResponse> send(List<ConversationContext> conversationContexts) {
        ensureApiKeyPresent();
        List<OpenAIRequest> requests = toRequests(conversationContexts);
        List<OpenAIResponse> responses = new ArrayList<>(requests.size());
        for (OpenAIRequest request : requests) {
            responses.add(callOpenAI(request));
        }
        return responses;
    }

    public List<OpenAIRequest> toRequests(List<ConversationContext> conversationContexts) {
        return conversationContexts.stream()
            .map(this::toRequest)
            .toList();
    }

    public OpenAIRequest toRequest(ConversationContext ctx) {
        Metadata metadata = new Metadata(
            ctx.analysisId(),
            ctx.chatId(),
            ctx.flaggedTimestamp()
        );

        Trigger trigger = new Trigger(
            ctx.flaggedMessage().getMessageId(),
            ctx.flaggedMessage().getSenderId(),
            ctx.flaggedMessage().getTimestamp(),
            extractText(ctx.flaggedMessage().getPayload()),
            ctx.flaggedMessage().getTotalScore(),
            ctx.flaggedMessage().getDecision(),
            ctx.flaggedMessage().getTriggeredRules()
        );

        return new OpenAIRequest(
            metadata,
            trigger,
            ctx.conversation()
        );
    }
    // TODO: see how you can make multiple
    private OpenAIResponse callOpenAI(OpenAIRequest request) {
        ChatCompletionRequest chatRequest = toChatCompletionRequest(request);

        return webClient.post()
            .uri(CHAT_COMPLETIONS_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(chatRequest)
            .retrieve()
            .bodyToMono(OpenAIResponse.class)
            .block(Duration.ofSeconds(30));
    }

    private ChatCompletionRequest toChatCompletionRequest(OpenAIRequest request) {
        List<OpenAIChatMessage> messages = new ArrayList<>();
        messages.add(new OpenAIChatMessage("system", systemPrompt()));
        messages.add(new OpenAIChatMessage("user", renderUserMessage(request)));

        return new ChatCompletionRequest(
            properties.getModel(),
            messages,
            properties.getTemperature(),
            properties.getMaxTokens(),
            JSON_OBJECT_FORMAT
        );
    }

    private String systemPrompt() {
        return """
            You are an expert fraud detection and online safety analysis model. 
            You will receive structured JSON containing:
            1. metadata,
            2. a trigger message that caused rule-based detection,
            3. a chronological conversation window.

            Your job is to evaluate whether the situation indicates fraud, coercion, 
            manipulation, impersonation, financial scam behavior, or other safety risks.

            You MUST respond ONLY in valid JSON. Do NOT include explanations outside the JSON.

            Your JSON response must strictly follow this schema:

            {
              "analysisId": string,
              "scamLikely": boolean,
              "threatLevel": "LOW" | "MEDIUM" | "HIGH",
              "scammerUserId": string | null,
              "victimUserId": string | null,
              "scamType": string | null,
              "summary": string,
              "recommendation": string
            }

            Do NOT reveal chain-of-thought. Provide the conclusions only.
            Base your analysis strictly on the conversation and rule hits provided.
            """;
    }

    private String renderUserMessage(OpenAIRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("metadata", metadataMap(request));
        body.put("trigger", triggerMap(request));
        body.put("conversation", conversationList(request.conversation()));

        try {
            return mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to render OpenAI user message as JSON", e);
        }
    }

    private Map<String, Object> metadataMap(OpenAIRequest request) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("analysisId", request.metadata().analysisId());
        metadata.put("chatId", request.metadata().chatId());
        metadata.put("flaggedTimestamp", request.metadata().flaggedTimestamp());
        metadata.put("conversationLength", request.conversation().size());
        return metadata;
    }

    private Map<String, Object> triggerMap(OpenAIRequest request) {
        Map<String, Object> trigger = new LinkedHashMap<>();
        trigger.put("messageId", request.trigger().messageId());
        trigger.put("senderId", request.trigger().senderId());
        trigger.put("timestamp", request.trigger().timestamp());
        trigger.put("text", request.trigger().text());
        trigger.put("totalScore", request.trigger().totalScore());
        trigger.put("decision", request.trigger().decision());
        trigger.put("ruleHits", renderRules(request.trigger().ruleResults()));
        return trigger;
    }

    private List<Map<String, Object>> conversationList(List<ChatTurn> turns) {
        List<Map<String, Object>> list = new ArrayList<>(turns.size());
        AtomicInteger idx = new AtomicInteger(1);
        for (ChatTurn turn : turns) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("index", idx.getAndIncrement());
            entry.put("senderId", turn.senderId());
            entry.put("timestamp", turn.timestamp());
            entry.put("messageId", turn.messageId());
            entry.put("text", turn.text());
            list.add(entry);
        }
        return list;
    }

    private List<Map<String, Object>> renderRules(List<RuleResult> rules) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (rules == null) {
            return list;
        }
        for (RuleResult rule : rules) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", rule.name());
            map.put("matched", rule.matched());
            map.put("score", rule.score());
            map.put("reason", rule.reason());
            list.add(map);
        }
        return list;
    }

    private void ensureApiKeyPresent() {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new IllegalStateException("Missing OpenAI API key (set openai.api-key or OPENAI_API_KEY)");
        }
    }

    public Optional<FraudAnalysisResult> parseFirstResult(OpenAIResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return Optional.empty();
        }
        String content = response.choices().getFirst().message().content();
        if (!StringUtils.hasText(content)) {
            return Optional.empty();
        }
        try {
            FraudAnalysisResult result = mapper.readValue(content, FraudAnalysisResult.class);
            return Optional.of(result);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse OpenAI fraud analysis JSON", e);
        }
    }

    private String extractText(Map<String, Object> payload) {
        if (payload == null) {
            return "";
        }
        Object value = payload.get("text");
        return value != null ? value.toString() : "";
    }
}
