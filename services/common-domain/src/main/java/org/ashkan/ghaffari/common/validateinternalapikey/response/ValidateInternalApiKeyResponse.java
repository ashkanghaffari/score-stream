package org.ashkan.ghaffari.common.validateinternalapikey.response;

import org.ashkan.ghaffari.common.integration.IntegrationApiKeyStatus;

public record ValidateInternalApiKeyResponse(
    boolean valid,
    String tenantId,
    String integrationId,
    IntegrationApiKeyStatus status,
    Long expiresAt,
    ValidateInternalApiKeyReason reason
) {}
