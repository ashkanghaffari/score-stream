package org.ashkan.ghaffari.adminconsole.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @Column(name = "token_hash", nullable = false, updatable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", nullable = false),
        @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    private TenantUser tenantUser;

    @Column(name = "tenant_id", nullable = false, updatable = false, insertable = false, length = 64)
    private String tenantId;

    @Column(name = "user_id", nullable = false, updatable = false, insertable = false, length = 64)
    private String userId;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    public RefreshToken() {}

    public RefreshToken(String token, TenantUser tenantUser, Instant issuedAt, Instant expiresAt, boolean revoked) {
        this.token = token;
        this.tenantUser = tenantUser;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public String getToken() {
        return token;
    }

    public TenantUser getTenantUser() {
        return tenantUser;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
