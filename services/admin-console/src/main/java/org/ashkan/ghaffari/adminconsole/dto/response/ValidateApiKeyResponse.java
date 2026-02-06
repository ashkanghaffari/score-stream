package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.common.integration.IntegrationApiKeyStatus;

public record ValidateApiKeyResponse(
    boolean valid,
    String tenantId,
    String integrationId,
    IntegrationApiKeyStatus status,
    Long expiresAt,
    ValidateApiKeyReason reason
) {}
