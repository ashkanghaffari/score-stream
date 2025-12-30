package org.ashkan.ghaffari.common.dynamo.tenantuser;

import org.ashkan.ghaffari.common.adminconsole.TenantUserRole;
import org.ashkan.ghaffari.common.adminconsole.TenantUserStatus;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
public class TenantUser {

    private String tenantId;
    private String userId;
    private String email;
    private TenantUserRole role;
    private TenantUserStatus status;
    private Long createdAt;

    public TenantUser() {}

    public TenantUser(String tenantId, String userId, String email,
                      TenantUserRole role, TenantUserStatus status, Long createdAt) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    @DynamoDbPartitionKey
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @DynamoDbSortKey
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "TenantUserByEmail")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TenantUserRole getRole() { return role; }
    public void setRole(TenantUserRole role) { this.role = role; }

    public TenantUserStatus getStatus() { return status; }
    public void setStatus(TenantUserStatus status) { this.status = status; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
