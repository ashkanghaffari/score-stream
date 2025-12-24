package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.common.dynamo.tenant.Tenant;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class TenantRepository {

    private final DynamoDbTable<Tenant> table;

    public TenantRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("Tenant",
            TableSchema.fromBean(Tenant.class));
    }

    public void save(Tenant tenant) { table.putItem(tenant); }

    public Tenant getById(String tenantId) {
        return table.getItem(
            Key.builder()
                .partitionValue(tenantId)
                .build()
        );
    }
}
