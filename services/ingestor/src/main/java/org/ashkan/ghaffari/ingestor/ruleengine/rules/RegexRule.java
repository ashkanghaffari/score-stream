package org.ashkan.ghaffari.ingestor.ruleengine.rules;

import org.ashkan.ghaffari.common.ruleengine.RuleResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexRule implements Rule {
    private final String name;
    private final Pattern pattern;
    private final int score;

    public RegexRule(String name, String regex, int score) {
        this.name = name;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.score = score;
    }

    @Override
    public RuleResult evaluate(String text) {
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            return new RuleResult(name, true, score, "Matched regex: " + pattern);
        }
        return new RuleResult(name, false, 0, "");
    }

    @Override
    public String getName() { return name; }
}
