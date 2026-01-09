package org.ashkan.ghaffari.adminconsole.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleTokenResponse(
    @NotBlank String access_token,
    @NotBlank String id_token,
    @NotBlank String token_type,
    @NotNull Integer expires_in,
    @NotBlank String scope
) {}
