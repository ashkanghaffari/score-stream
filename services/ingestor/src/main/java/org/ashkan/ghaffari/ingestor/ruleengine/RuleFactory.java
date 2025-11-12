package org.ashkan.ghaffari.ingestor.ruleengine;

import org.ashkan.ghaffari.ingestor.dynamo.ruleconfig.RuleConfig;
import org.ashkan.ghaffari.ingestor.ruleengine.rules.KeywordRule;
import org.ashkan.ghaffari.ingestor.ruleengine.rules.RegexRule;
import org.ashkan.ghaffari.ingestor.ruleengine.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class RuleFactory {

    public RuleFactory() {}

    public Rule build(RuleConfig cfg) {
        return switch (cfg.getType().toLowerCase()) {
            case "keyword" -> new KeywordRule(cfg.getName(), cfg.getPatterns(), cfg.getScore());
            case "regex" -> new RegexRule(cfg.getName(), cfg.getPatterns().getFirst(), cfg.getScore());
            default -> throw new IllegalArgumentException("Unknown rule type: " + cfg.getType());
        };
    }
}
