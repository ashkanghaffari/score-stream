package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.common.adminconsole.IntegrationStatus;
import org.ashkan.ghaffari.common.adminconsole.IntegrationType;

public record IntegrationResponse(
    String tenantId,
    String integrationId,
    String name,
    IntegrationType type,
    IntegrationStatus status,
    Long createdAt
) {}
