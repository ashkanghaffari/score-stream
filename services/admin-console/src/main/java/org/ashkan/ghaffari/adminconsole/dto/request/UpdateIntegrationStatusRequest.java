package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotNull;
import org.ashkan.ghaffari.common.adminconsole.IntegrationStatus;

public record UpdateIntegrationStatusRequest(
    @NotNull IntegrationStatus status
) {}
