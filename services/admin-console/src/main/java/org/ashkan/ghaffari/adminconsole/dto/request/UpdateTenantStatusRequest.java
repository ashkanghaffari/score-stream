package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotNull;
import org.ashkan.ghaffari.adminconsole.entity.TenantStatus;

public record UpdateTenantStatusRequest(
    @NotNull TenantStatus status
) {}
