package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
   @NotBlank
   @Size(min = 3, max = 63)
   @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Tenant name must be lowercase letters, numbers, and dashes")
   String name
) {}
