package org.ashkan.ghaffari.common.ws.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record ChatTextMessage(
   UUID id,
   String idempotencyId,
   MessageType type,
   String tenantId,
   String appId,
   String chatId,
   String senderId,
   Instant timestamp,
   JsonNode payload
) {}
