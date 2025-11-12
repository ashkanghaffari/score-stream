package org.ashkan.ghaffari.ingestor.dynamo.ruleconfig;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
public class RuleConfigRepository {

    private final DynamoDbTable<RuleConfig> table;

    public RuleConfigRepository(DynamoDbEnhancedClient client) {
        this.table = client.table("RuleConfiguration", TableSchema.fromBean(RuleConfig.class));
    }

    public List<RuleConfig> loadAll() {
        return table.scan().items().stream()
            .filter(RuleConfig::isEnabled)
            .toList();
    }
}
