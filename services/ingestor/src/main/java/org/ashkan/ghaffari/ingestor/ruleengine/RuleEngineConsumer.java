package org.ashkan.ghaffari.ingestor.ruleengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.ruleengine.sanitation.ProcessedTextResult;
import org.ashkan.ghaffari.ingestor.ruleengine.sanitation.TextProcessor;
import org.ashkan.ghaffari.ingestor.ws.dto.ChatMessagePayload;
import org.ashkan.ghaffari.ingestor.ws.dto.ChatTextMessage;
import org.ashkan.ghaffari.ingestor.ws.dto.FlaggedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class RuleEngineConsumer {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineConsumer.class);
    private static final String CLEAN_TOPIC = "text-clean";
    private static final String FLAGGED_TOPIC = "text-flagged";
    private static final String RAW_TOPIC = "text-raw";
    private static final String RULE_ENGINE_CONSUMER_GROUP_ID = "rule-engine-consumer";

    private final KafkaTemplate<String, byte[]> kafka;
    private final ObjectMapper mapper;
    private final TextProcessor textProcessor;
    private final RuleEngine ruleEngine;


    public RuleEngineConsumer(KafkaTemplate<String, byte[]> kafka, ObjectMapper mapper, TextProcessor textProcessor,
                              RuleEngine ruleEngine) {
        this.kafka = kafka;
        this.mapper = mapper;
        this.textProcessor = textProcessor;
        this.ruleEngine = ruleEngine;
    }

    @KafkaListener(topics = RAW_TOPIC, groupId = RULE_ENGINE_CONSUMER_GROUP_ID)
    public void onMessage(byte[] data) throws IOException {
        ChatTextMessage message = mapper.readValue(data, ChatTextMessage.class);
        String textValue = extractPayloadText(message.payload());
        EvaluationResult evaluationResult = textEvalResult(textValue);

        if (!"ALLOW".equalsIgnoreCase(evaluationResult.decision())) {
            FlaggedMessage flaggedMessage = new FlaggedMessage(
                message.id(),
                message.idempotencyId(),
                message.chatId(),
                message.senderId(),
                message.timestamp(),
                evaluationResult.totalScore(),
                evaluationResult.decision(),
                evaluationResult.triggered(),
                message.payload()
            );
            sendMessageToKafka(flaggedMessage, FLAGGED_TOPIC);
            }

        sendMessageToKafka(message, CLEAN_TOPIC);
    }

    public EvaluationResult textEvalResult(String raw) {
        Objects.requireNonNull(raw, "raw input cannot be null");
        try {
            ProcessedTextResult processedTextResult = textProcessor.process(raw);
            Objects.requireNonNull(processedTextResult, "processTextResult cannot be null");
            return ruleEngine.evaluate(processedTextResult.normalizedText());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to process text for rule evaluation", ex);
        }
    }

    private String extractPayloadText(JsonNode payload) {
        JsonNode textNode = payload.get("text");
        if (textNode == null) {
            return null;
        }
        String value = textNode.asText();
        return value != null ? value.trim() : null;
    }

    private void sendMessageToKafka(ChatMessagePayload payload, String topic) {
        try {
            byte[] serialized = mapper.writeValueAsBytes(payload);
            kafka.send(topic, payload.chatId(), serialized)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.warn("Kafka send failed for topic {}: {}", topic, ex.toString());
                    }
                });
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize payload for topic " + topic, ex);
        }
    }
}
