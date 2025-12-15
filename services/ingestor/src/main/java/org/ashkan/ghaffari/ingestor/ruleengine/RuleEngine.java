package org.ashkan.ghaffari.ingestor.ruleengine;

import org.ashkan.ghaffari.common.ruleengine.RuleResult;
import org.ashkan.ghaffari.ingestor.ruleengine.rules.Rule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RuleEngine {
    private volatile Map<String, List<Rule>> rulesByTenant;

    public void loadRules(Map<String, List<Rule>> newRules) {
        this.rulesByTenant = newRules;
    }

    public EvaluationResult evaluate(String text, String tenantId) {
        int total = 0;
        List<RuleResult> triggered = new ArrayList<>();
        List<Rule> rules = rulesByTenant != null
            ? rulesByTenant.getOrDefault(tenantId, List.of())
            : List.of();

        for (Rule rule : rules) {
            var result = rule.evaluate(text);
            if (result.matched()) {
                total += result.score();
                triggered.add(result);
            }
        }

//        String decision = (total >= 80) ? "BLOCK"
//            : (total >= 60) ? "FLAG"
//            : "ALLOW";
        String decision = null;
        if (!triggered.isEmpty()) {
            decision = "FLAG";
        } else {
            decision = "ALLOW";
        }


        return new EvaluationResult(total, triggered, decision);
    }
}
