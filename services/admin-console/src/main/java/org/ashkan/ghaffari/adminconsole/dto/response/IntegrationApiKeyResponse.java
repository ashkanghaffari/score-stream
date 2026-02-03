package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKeyStatus;

public record IntegrationApiKeyResponse(
    String apiKeyId,
    String keyHash,
    IntegrationApiKeyStatus status,
    Long createdAt,
    Long expiresAt,
    Long lastUsedAt
) {}
