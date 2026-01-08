package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationType;

public record CreateIntegrationRequest(
    @NotBlank String name,
    @NotNull IntegrationType type
) {}
