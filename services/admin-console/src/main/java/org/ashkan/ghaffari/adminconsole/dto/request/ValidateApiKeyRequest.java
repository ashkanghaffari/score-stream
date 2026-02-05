package org.ashkan.ghaffari.adminconsole.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateApiKeyRequest(
    @NotBlank String keyHash
) {}
