package org.ashkan.ghaffari.ingestor.ws.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngestMessage(
    UUID id,
    String idempotencyId,
    String type,
    String chatId,
    String senderId,
    long timestamp,
    JsonNode payload
) {
    public IngestMessage {
        // Required non-null fields
        if (idempotencyId == null || idempotencyId.isBlank()) {
            throw new IllegalArgumentException("idempotencyId is required");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type is required");
        }
        if (!"TEXT".equals(type)) {
            throw new IllegalArgumentException("Unsupported event type: " + type);
        }
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId is required");
        }
        if (senderId == null || senderId.isBlank()) {
            throw new IllegalArgumentException("senderId is required");
        }
        if (timestamp <= 0) {
            throw new IllegalArgumentException("timestamp must be > 0");
        }
    }
}
