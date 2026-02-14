package org.ashkan.ghaffari.adminconsole.service;

import jakarta.transaction.Transactional;
import org.ashkan.ghaffari.adminconsole.dto.response.RefreshTokenResult;
import org.ashkan.ghaffari.adminconsole.entity.RefreshToken;
import org.ashkan.ghaffari.adminconsole.entity.TenantUser;
import org.ashkan.ghaffari.adminconsole.repository.RefreshTokenRepository;
import org.ashkan.ghaffari.adminconsole.security.config.properties.JwtTokenProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProperties jwtConfig;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProperties jwtConfig) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtConfig = jwtConfig;
    }

    @Transactional
    public RefreshTokenResult create(TenantUser user) {
        String plainToken = generatePlainToken();
        RefreshToken entity = new RefreshToken(
            hashToken(plainToken),
            user,
            Instant.now(),
            Instant.now().plus(jwtConfig.getRefreshTokenLifetimeDays(), ChronoUnit.DAYS),
            false
        );
        refreshTokenRepository.save(entity);
        entity.setRevoked(false);
        return new RefreshTokenResult(
            entity,
            plainToken
        );
    }

    public boolean validRefreshTokenExists(TenantUser user) {
        return refreshTokenRepository.findAllByTenantUser(user)
            .stream()
            .anyMatch(RefreshTokenService::isRefreshTokenValid);
    }

    public String generatePlainToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Error hashing refresh token", e);
        }
    }

    public RefreshToken validate(String plainToken) {
        String hashed = hashToken(plainToken);
        RefreshToken token = refreshTokenRepository.findById(hashed)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (!isRefreshTokenValid(token)) {
            revokeAll(token.getTenantUser());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        return token;
    }

    public static boolean isRefreshTokenValid(RefreshToken token) {
        return !token.isRevoked() && token.getExpiresAt().isAfter(Instant.now());
    }

    @Transactional
    public RefreshTokenResult rotate(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        return create(oldToken.getTenantUser());
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public void revokeAll(TenantUser user) {
        refreshTokenRepository.findAllByTenantUser(user)
            .forEach(this::revoke);
    }
}
