package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKeyStatus;

public record CreateIntegrationApiKeyResponse(
    String apiKeyId,
    String apiKey,
    IntegrationApiKeyStatus status,
    Long createdAt,
    Long expiresAt
) {}
