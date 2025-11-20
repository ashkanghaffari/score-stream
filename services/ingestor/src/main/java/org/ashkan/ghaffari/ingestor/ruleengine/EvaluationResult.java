package org.ashkan.ghaffari.ingestor.ruleengine;

import org.ashkan.ghaffari.common.ruleengine.RuleResult;
import java.util.List;

public record EvaluationResult(
    int totalScore,
    List<RuleResult> triggered,
    String decision
) {}
