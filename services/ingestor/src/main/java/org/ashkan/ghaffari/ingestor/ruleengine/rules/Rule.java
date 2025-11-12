package org.ashkan.ghaffari.ingestor.ruleengine.rules;

import org.ashkan.ghaffari.ingestor.ruleengine.RuleResult;

public interface Rule {
    RuleResult evaluate(String text);
    String getName();
}
