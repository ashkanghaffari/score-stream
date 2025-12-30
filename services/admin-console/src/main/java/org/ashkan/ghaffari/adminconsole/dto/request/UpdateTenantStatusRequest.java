package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.ashkan.ghaffari.common.adminconsole.TenantStatus;

public record UpdateTenantStatusRequest(
    @NotBlank TenantStatus status
) {}
