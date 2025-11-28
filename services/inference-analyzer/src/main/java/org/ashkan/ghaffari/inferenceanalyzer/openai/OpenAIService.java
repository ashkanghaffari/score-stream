package org.ashkan.ghaffari.inferenceanalyzer.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.inferenceanalyzer.model.ConversationContext;
import org.ashkan.ghaffari.inferenceanalyzer.openai.client.OpenAIClient;
import org.ashkan.ghaffari.inferenceanalyzer.openai.config.OpenAIProperties;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.OpenAIRequest;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.FraudAnalysisResult;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.Metadata;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.response.OpenAIResponse;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request.Trigger;
import org.ashkan.ghaffari.inferenceanalyzer.openai.prompt.PromptBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final PromptBuilder promptBuilder;
    private final OpenAIProperties properties;
    private final ObjectMapper mapper;

    public OpenAIService(OpenAIClient openAIClient,
                         PromptBuilder promptBuilder,
                         OpenAIProperties properties,
                         ObjectMapper mapper) {
        this.openAIClient = openAIClient;
        this.promptBuilder = promptBuilder;
        this.properties = properties;
        this.mapper = mapper;
    }

    public List<FraudAnalysisResult> sendAndParse(List<ConversationContext> conversationContexts) {
        List<OpenAIResponse> responses = send(conversationContexts);
        return responses.stream()
            .map(this::parseFirstResult)
            .flatMap(Optional::stream)
            .toList();
    }

    public List<OpenAIResponse> send(List<ConversationContext> conversationContexts) {
        ensureApiKeyPresent();
        List<OpenAIResponse> responses = new ArrayList<>(conversationContexts.size());
        for (ConversationContext context : conversationContexts) {
            OpenAIRequest request = toRequest(context);
            responses.add(callOpenAI(request));
        }
        return responses;
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

    private OpenAIResponse callOpenAI(OpenAIRequest request) {
        return openAIClient.complete(promptBuilder.build(request));
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
        } catch (JsonProcessingException e) {
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
