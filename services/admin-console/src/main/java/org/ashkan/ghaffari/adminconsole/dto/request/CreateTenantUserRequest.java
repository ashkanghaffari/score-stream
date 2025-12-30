package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ashkan.ghaffari.common.adminconsole.TenantUserRole;

public record CreateTenantUserRequest(
    @NotBlank @Email String email,
    @NotNull TenantUserRole role
) {}
