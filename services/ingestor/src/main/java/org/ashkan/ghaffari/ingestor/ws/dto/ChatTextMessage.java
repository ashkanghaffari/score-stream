package org.ashkan.ghaffari.ingestor.ws.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record ChatTextMessage(
   UUID id,
   String idempotencyId,
   MessageType type,
   String chatId,
   String senderId,
   Instant timestamp,
   JsonNode payload
) {}
