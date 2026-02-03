package org.ashkan.ghaffari.adminconsole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "integration_api_key",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_integration_api_key_hash", columnNames = "key_hash")
    }
)
@IdClass(IntegrationApiKeyId.class)
public class IntegrationApiKey {

    @Id
    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Id
    @Column(name = "integration_id", nullable = false, length = 64)
    private String integrationId;

    @Id
    @Column(name = "api_key_id", nullable = false, length = 64)
    private String apiKeyId;

    @Column(name = "key_hash", nullable = false, length = 255)
    private String keyHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private IntegrationApiKeyStatus status;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "expires_at")
    private Long expiresAt;

    @Column(name = "last_used_at")
    private Long lastUsedAt;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false),
        @JoinColumn(name = "integration_id", referencedColumnName = "integration_id", insertable = false, updatable = false)
    })
    private Integration integration;

    public IntegrationApiKey() {}

    public IntegrationApiKey(String tenantId, String integrationId, String apiKeyId, String keyHash,
                             IntegrationApiKeyStatus status, Long createdAt, Long expiresAt, Long lastUsedAt) {
        this.tenantId = tenantId;
        this.integrationId = integrationId;
        this.apiKeyId = apiKeyId;
        this.keyHash = keyHash;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
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

    public String getApiKeyId() {
        return apiKeyId;
    }

    public void setApiKeyId(String apiKeyId) {
        this.apiKeyId = apiKeyId;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public IntegrationApiKeyStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationApiKeyStatus status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Integration getIntegration() {
        return integration;
    }

    public void setIntegration(Integration integration) {
        this.integration = integration;
    }
}
