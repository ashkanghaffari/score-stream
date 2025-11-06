package org.ashkan.ghaffari.ingestor.ws.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record PublishMessage(
    UUID id,
    String idempotencyId,
    String type,
    String chatId,
    String userId,
    long timestamp,
    JsonNode payload
) {}
