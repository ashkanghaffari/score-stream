package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationType;

public record CreateIntegrationRequest(
    @NotBlank
    @Size(min = 3, max = 63)
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Integration name must be lowercase letters, numbers, and dashes")
    String name,
    @NotNull IntegrationType type
) {}
