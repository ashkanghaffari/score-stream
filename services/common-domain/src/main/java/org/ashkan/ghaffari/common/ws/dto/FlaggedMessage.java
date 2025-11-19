package org.ashkan.ghaffari.common.ws.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.ashkan.ghaffari.common.ruleengine.RuleResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FlaggedMessage(
    UUID messageId,
    String idempotencyId,
    String chatId,
    String senderId,
    Instant timestamp,
    int totalScore,
    String decision,
    List<RuleResult> triggeredRules,
    JsonNode payload
) implements ChatMessagePayload {}
