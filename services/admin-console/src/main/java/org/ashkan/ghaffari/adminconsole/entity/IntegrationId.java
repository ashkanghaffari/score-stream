package org.ashkan.ghaffari.adminconsole.entity;

import java.io.Serializable;
import java.util.Objects;

public class IntegrationId implements Serializable {

    private String tenantId;
    private String integrationId;

    public IntegrationId() {}

    public IntegrationId(String tenantId, String integrationId) {
        this.tenantId = tenantId;
        this.integrationId = integrationId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntegrationId that = (IntegrationId) o;
        return Objects.equals(tenantId, that.tenantId)
            && Objects.equals(integrationId, that.integrationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, integrationId);
    }
}
