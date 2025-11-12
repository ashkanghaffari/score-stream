package org.ashkan.ghaffari.ingestor.ruleengine.rules;

import org.ashkan.ghaffari.ingestor.ruleengine.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KeywordRule implements Rule {

    private static final Logger log = LoggerFactory.getLogger(KeywordRule.class);
    private final String name;
    private final List<String> keywords;
    private final int score;

    public KeywordRule(String name, List<String> keywords, int score) {
        this.name = name;
        this.keywords = keywords;
        this.score = score;
    }

    @Override
    public RuleResult evaluate(String text) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return new RuleResult(name, true, score, "Found keyword: " + kw);
            }
        }
        return new RuleResult(name, false, 0, "");
    }

    @Override
    public String getName() { return name; }
}
