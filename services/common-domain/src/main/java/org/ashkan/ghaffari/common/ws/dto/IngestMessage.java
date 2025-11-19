package org.ashkan.ghaffari.common.ws.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngestMessage(
    String idempotencyId,
    MessageType type,
    String chatId,
    String senderId,
    JsonNode payload
) {
    public IngestMessage {
        if (idempotencyId == null || idempotencyId.isBlank()) {
            throw new IllegalArgumentException("idempotencyId is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (type != MessageType.TEXT) {
            throw new IllegalArgumentException("Unsupported event type: " + type);
        }
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId is required");
        }
        if (senderId == null || senderId.isBlank()) {
            throw new IllegalArgumentException("senderId is required");
        }
    }
}
