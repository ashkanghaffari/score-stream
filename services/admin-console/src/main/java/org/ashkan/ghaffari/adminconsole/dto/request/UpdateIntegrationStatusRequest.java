package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotNull;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationStatus;

public record UpdateIntegrationStatusRequest(
    @NotNull IntegrationStatus status
) {}
