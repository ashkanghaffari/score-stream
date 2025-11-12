package org.ashkan.ghaffari.ingestor.ruleengine;

import org.ashkan.ghaffari.ingestor.dynamo.ruleconfig.RuleConfig;
import org.ashkan.ghaffari.ingestor.dynamo.ruleconfig.RuleConfigRepository;
import org.ashkan.ghaffari.ingestor.ruleengine.rules.Rule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleLoader implements CommandLineRunner {

    private final RuleConfigRepository repo;
    private final RuleEngine engine;
    private final RuleFactory ruleFactory;

    public RuleLoader(RuleConfigRepository repo, RuleEngine engine, RuleFactory ruleFactory) {
        this.repo = repo;
        this.engine = engine;
        this.ruleFactory = ruleFactory;
    }

    @Override
    public void run(String... args) {
        List<RuleConfig> configs = repo.loadAll();
        List<Rule> rules = configs.stream()
            .map(ruleFactory::build)
            .toList();
        engine.loadRules(rules);
        System.out.printf("Loaded %d rules from DynamoDB%n", rules.size());
    }
}