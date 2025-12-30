package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ashkan.ghaffari.common.adminconsole.IntegrationType;

public record CreateIntegrationRequest(
    @NotBlank String name,
    @NotNull IntegrationType type
) {}
