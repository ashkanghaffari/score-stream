package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.common.dynamo.tenantuser.TenantUser;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class TenantUserRepository {

    private final DynamoDbTable<TenantUser> table;

    public TenantUserRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("TenantUser", TableSchema.fromBean(TenantUser.class));
    }

    public void save(TenantUser tenantUser) {
        table.putItem(tenantUser);
    }

    public TenantUser find(String tenantId, String userId) {
        return table.getItem(
            Key.builder()
                .partitionValue(tenantId)
                .sortValue(userId)
                .build()
        );
    }
}
