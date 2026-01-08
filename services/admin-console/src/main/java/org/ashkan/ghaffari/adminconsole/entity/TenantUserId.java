package org.ashkan.ghaffari.adminconsole.entity;

import java.io.Serializable;
import java.util.Objects;

public class TenantUserId implements Serializable {

    private String tenantId;
    private String userId;

    public TenantUserId() {}

    public TenantUserId(String tenantId, String userId) {
        this.tenantId = tenantId;
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TenantUserId that = (TenantUserId) o;
        return Objects.equals(tenantId, that.tenantId)
            && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, userId);
    }
}
