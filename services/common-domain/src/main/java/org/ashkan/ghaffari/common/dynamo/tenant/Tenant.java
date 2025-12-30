package org.ashkan.ghaffari.common.dynamo.tenant;

import org.ashkan.ghaffari.common.adminconsole.TenantStatus;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Tenant {

    private String tenantId;
    private String name;
    private TenantStatus tenantStatus;
    private Long createdAt;
    private Long deletedAt;

    public Tenant() {}

    public Tenant(String tenantId, String name, TenantStatus tenantStatus,
                  Long createdAt, Long deletedAt) {
        this.tenantId = tenantId;
        this.name = name;
        this.tenantStatus = tenantStatus;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    @DynamoDbPartitionKey
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TenantStatus getStatus() { return tenantStatus; }
    public void setStatus(TenantStatus tenantStatus) { this.tenantStatus = tenantStatus; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Long deletedAt) { this.deletedAt = deletedAt; }
}
