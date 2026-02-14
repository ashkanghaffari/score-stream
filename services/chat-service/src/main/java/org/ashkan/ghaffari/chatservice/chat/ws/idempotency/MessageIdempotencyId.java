package org.ashkan.ghaffari.chatservice.chat.ws.idempotency;

import java.util.Objects;

public record MessageIdempotencyId(String value) {

    private static final String DELIMITER = ":";

    public static MessageIdempotencyId of(
        String tenantId,
        String appId,
        String idempotencyId
    ) {
        validateInput(tenantId, "tenantId");
        validateInput(appId, "appId");
        validateInput(idempotencyId, "idempotencyId");

        return new MessageIdempotencyId(String.join(DELIMITER, tenantId, appId, idempotencyId));
    }

    private static void validateInput(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " cannot be null or blank");
    }

    public MessageIdempotencyId {
        Objects.requireNonNull(value, "Idempotency key cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("Idempotency key cannot be blank");
        }
    }
}