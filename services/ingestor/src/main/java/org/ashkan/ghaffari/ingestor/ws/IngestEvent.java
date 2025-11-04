package org.ashkan.ghaffari.ingestor.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngestEvent(
    String idempotencyId, // unique per message
    String type,          // TEXT
    String chatId,
    String userId,
    long timestamp,
    JsonNode payload
) {}
