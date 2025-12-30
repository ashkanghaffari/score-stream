package org.ashkan.ghaffari.common.dynamo.integration;

import org.ashkan.ghaffari.common.adminconsole.IntegrationStatus;
import org.ashkan.ghaffari.common.adminconsole.IntegrationType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
public class Integration {

    private String tenantId;
    private String integrationId;
    private String name;
    private IntegrationType type;
    private String apiKeyHash;
    private IntegrationStatus status;
    private Long createdAt;

    public Integration() {}

    public Integration(String tenantId, String integrationId, String name,
                       IntegrationType type, String apiKeyHash, IntegrationStatus status,
                       Long createdAt) {
        this.tenantId = tenantId;
        this.integrationId = integrationId;
        this.name = name;
        this.type = type;
        this.apiKeyHash = apiKeyHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    @DynamoDbPartitionKey
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @DynamoDbSortKey
    public String getIntegrationId() { return integrationId; }
    public void setIntegrationId(String integrationId) { this.integrationId = integrationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public IntegrationType getType() { return type; }
    public void setType(IntegrationType type) { this.type = type; }

    @DynamoDbSecondaryPartitionKey(indexNames = "IntegrationByApiKeyHash")
    public String getApiKeyHash() { return apiKeyHash; }
    public void setApiKeyHash(String apiKeyHash) { this.apiKeyHash = apiKeyHash; }

    public IntegrationStatus getStatus() { return status; }
    public void setStatus(IntegrationStatus status) { this.status = status; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
