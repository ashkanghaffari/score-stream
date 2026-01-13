package org.ashkan.ghaffari.adminconsole.dto.response;

public record GenerateTokenResponse(
    String access_token,
    String token_type,
    String refresh_token,
    long expires_in,
    String scope,
    UserInfo user
) {}
