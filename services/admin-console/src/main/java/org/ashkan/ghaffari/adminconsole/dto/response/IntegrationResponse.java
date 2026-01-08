package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.adminconsole.entity.IntegrationStatus;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationType;

public record IntegrationResponse(
    String tenantId,
    String integrationId,
    String name,
    IntegrationType type,
    IntegrationStatus status,
    Long createdAt
) {}
