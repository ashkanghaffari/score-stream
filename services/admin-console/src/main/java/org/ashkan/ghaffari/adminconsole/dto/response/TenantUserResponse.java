package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.adminconsole.entity.TenantUserRole;
import org.ashkan.ghaffari.adminconsole.entity.TenantUserStatus;

public record TenantUserResponse(
    String tenantId,
    String userId,
    String email,
    TenantUserRole role,
    TenantUserStatus status,
    Long createdAt
) {}
