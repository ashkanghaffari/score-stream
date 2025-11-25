package org.ashkan.ghaffari.inferenceanalyzer.openai.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.ruleengine.RuleResult;
import org.ashkan.ghaffari.inferenceanalyzer.ChatTurn;
import org.ashkan.ghaffari.inferenceanalyzer.openai.config.OpenAIProperties;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.ChatCompletionRequest;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.OpenAIChatMessage;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.OpenAIRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PromptBuilder {

    private static final ChatCompletionRequest.ResponseFormat JSON_OBJECT_FORMAT =
        new ChatCompletionRequest.ResponseFormat("json_object");

    private final OpenAIProperties properties;
    private final ObjectMapper mapper;

    public PromptBuilder(OpenAIProperties properties, ObjectMapper mapper) {
        this.properties = properties;
        this.mapper = mapper;
    }

    public ChatCompletionRequest build(OpenAIRequest request) {
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
}
