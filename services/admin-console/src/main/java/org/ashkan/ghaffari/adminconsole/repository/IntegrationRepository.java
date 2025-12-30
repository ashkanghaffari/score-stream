package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.common.dynamo.integration.Integration;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.List;

@Repository
public class IntegrationRepository {

    private final DynamoDbTable<Integration> table;

    public IntegrationRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("Integration", TableSchema.fromBean(Integration.class));
    }

    public void save(Integration integration) {
        table.putItem(integration);
    }

    public Integration find(String tenantId, String integrationId) {
        return table.getItem(
            Key.builder()
                .partitionValue(tenantId)
                .sortValue(integrationId)
                .build()
        );
    }

    public List<Integration> findByTenant(String tenantId) {
        PageIterable<Integration> pages = table.query(r -> r
            .queryConditional(
                software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(tenantId).build()
                )
            )
        );
        return pages.items().stream().toList();
    }
}
