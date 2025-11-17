package org.ashkan.ghaffari.ingestor.ruleengine;

public record RuleResult(
    String name,
    boolean matched,
    int score,
    String reason
) {}
