package org.ashkan.ghaffari.adminconsole.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.ashkan.ghaffari.adminconsole.security.token.JwtTokenConfig;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtTokenConfig config;
    private final SecretKey key;

    public JwtService(JwtTokenConfig config) {
        this.config = config;
        this.key = Keys.hmacShaKeyFor(config.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String tenantId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(config.getAccessTokenLifetimeSec());

        return Jwts.builder()
            .subject(userId)
            .issuer(config.getIssuer())
            .claims(Map.of(
                "tenantId", tenantId,
                "role", role
            ))
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
