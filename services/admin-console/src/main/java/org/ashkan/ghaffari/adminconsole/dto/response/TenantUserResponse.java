package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.common.adminconsole.TenantUserRole;
import org.ashkan.ghaffari.common.adminconsole.TenantUserStatus;

public record TenantUserResponse(
    String tenantId,
    String userId,
    String email,
    TenantUserRole role,
    TenantUserStatus status,
    Long createdAt
) {}
