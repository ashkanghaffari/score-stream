package org.ashkan.ghaffari.adminconsole.entity;

import java.io.Serializable;
import java.util.Objects;

public class IntegrationApiKeyId implements Serializable {
    private String tenantId;
    private String integrationId;
    private String apiKeyId;

    public IntegrationApiKeyId() {}

    public IntegrationApiKeyId(String tenantId, String integrationId, String apiKeyId) {
        this.tenantId = tenantId;
        this.integrationId = integrationId;
        this.apiKeyId = apiKeyId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntegrationApiKeyId that = (IntegrationApiKeyId) o;
        return Objects.equals(tenantId, that.tenantId)
            && Objects.equals(integrationId, that.integrationId)
            && Objects.equals(apiKeyId, that.apiKeyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, integrationId, apiKeyId);
    }
}
