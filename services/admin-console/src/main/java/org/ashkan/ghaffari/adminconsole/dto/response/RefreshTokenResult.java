package org.ashkan.ghaffari.adminconsole.dto.response;

import org.ashkan.ghaffari.adminconsole.entity.RefreshToken;

public record RefreshTokenResult(RefreshToken entity, String plainToken) {}
