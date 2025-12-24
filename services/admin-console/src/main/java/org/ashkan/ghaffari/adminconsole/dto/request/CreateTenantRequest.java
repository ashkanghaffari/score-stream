package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest(
   @NotBlank String name
) {}
