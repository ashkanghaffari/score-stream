package org.ashkan.ghaffari.inferenceanalyzer.openai.dto.request;

import org.ashkan.ghaffari.common.ruleengine.RuleResult;

import java.util.List;

public record Trigger(
    String messageId,
    String senderId,
    long timestamp,
    String text,
    int totalScore,
    String decision,
    List<RuleResult> ruleResults
) {}
