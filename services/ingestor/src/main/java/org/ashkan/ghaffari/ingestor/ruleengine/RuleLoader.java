package org.ashkan.ghaffari.ingestor.ruleengine;

import org.ashkan.ghaffari.common.dynamo.ruleconfig.RuleConfig;
import org.ashkan.ghaffari.ingestor.dynamo.ruleconfig.RuleConfigRepository;
import org.ashkan.ghaffari.ingestor.ruleengine.rules.Rule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<String, List<Rule>> rulesByTenant = getRuleConfigs();
        engine.loadRules(rulesByTenant);

        int totalRules = rulesByTenant.values().stream()
            .mapToInt(List::size)
            .sum();

        System.out.printf("Loaded %d rules from DynamoDB%n", totalRules);
    }

    public void reload() {
        engine.loadRules(getRuleConfigs());
    }

    private Map<String, List<Rule>> getRuleConfigs() {
        return repo.loadAll().stream()
            .collect(Collectors.groupingBy(
                RuleConfig::getTenantId,
                Collectors.mapping(ruleFactory::build, Collectors.toList())
            ));
    }
}