package org.ashkan.ghaffari.ingestor.ruleengine;

import java.util.List;

public record EvaluationResult(
    int totalScore,
    List<RuleResult> triggered,
    String decision
) {}
