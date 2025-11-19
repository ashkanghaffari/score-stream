package org.ashkan.ghaffari.common.ruleengine;

public record RuleResult(
    String name,
    boolean matched,
    int score,
    String reason
) {}
