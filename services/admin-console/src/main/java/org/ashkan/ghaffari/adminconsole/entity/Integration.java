package org.ashkan.ghaffari.adminconsole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "integration",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_integration_api_key_hash", columnNames = "api_key_hash")
    }
)
@IdClass(IntegrationId.class)
public class Integration {

    @Id
    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Id
    @Column(name = "integration_id", nullable = false, length = 64)
    private String integrationId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private IntegrationType type;

    @Column(name = "api_key_hash", length = 255)
    private String apiKeyHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private IntegrationStatus status;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @ManyToOne
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    private Tenant tenant;

    public Integration() {}

    public Integration(String tenantId, String integrationId, String name, IntegrationType type,
                       String apiKeyHash, IntegrationStatus status, Long createdAt) {
        this.tenantId = tenantId;
        this.integrationId = integrationId;
        this.name = name;
        this.type = type;
        this.apiKeyHash = apiKeyHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IntegrationType getType() {
        return type;
    }

    public void setType(IntegrationType type) {
        this.type = type;
    }

    public String getApiKeyHash() {
        return apiKeyHash;
    }

    public void setApiKeyHash(String apiKeyHash) {
        this.apiKeyHash = apiKeyHash;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
