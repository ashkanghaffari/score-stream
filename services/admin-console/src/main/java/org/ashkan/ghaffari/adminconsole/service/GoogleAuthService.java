package org.ashkan.ghaffari.adminconsole.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.ashkan.ghaffari.adminconsole.dto.request.GoogleTokenRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.GenerateTokenResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.GoogleTokenResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.RefreshTokenResult;
import org.ashkan.ghaffari.adminconsole.dto.response.SessionInfo;
import org.ashkan.ghaffari.adminconsole.dto.response.UserInfo;
import org.ashkan.ghaffari.adminconsole.security.config.GoogleIdpConfig;
import org.ashkan.ghaffari.adminconsole.security.token.JwtTokenConfig;
import org.ashkan.ghaffari.adminconsole.entity.RefreshToken;
import org.ashkan.ghaffari.adminconsole.entity.TenantUser;
import org.ashkan.ghaffari.adminconsole.exception.InvalidExternalClaimException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoogleAuthService {

    private static final long CLOCK_SKEW_SECONDS = 60;

    private final GoogleIdpConfig googleConfig;
    private final JwtTokenConfig jwtConfig;
    private final TenantUserService tenantUserService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final Validator validator;

    public GoogleAuthService(
        GoogleIdpConfig googleConfig,
        JwtTokenConfig jwtConfig,
        TenantUserService tenantUserService,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        Validator validator) {
        this.googleConfig = googleConfig;
        this.jwtConfig = jwtConfig;
        this.tenantUserService = tenantUserService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.validator = validator;
    }

    public GenerateTokenResponse callback(String code, String expectedNonce, String tenantId) throws Exception {
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing tenantId");
        }

        GoogleTokenResponse tokenResponse = exchangeAuthorizationCode(code);
        validateTokenResponse(tokenResponse);

        String idToken = tokenResponse.id_token();
        JWTClaimsSet claims = verifyGoogleToken(idToken, expectedNonce);

        String email = claims.getStringClaim("email");
        String name = claims.getStringClaim("name");
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID token missing email");
        }
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID token missing name");
        }

        // Allowlist enforcement: only active users added for this tenant can login.
        TenantUser user = tenantUserService.requireActiveUser(tenantId, email);

        // Issue new app tokens
        String accessToken = jwtService.generateToken(
            user.getUserId(),
            user.getTenantId(),
            user.getRole().name());

        RefreshTokenResult refreshTokenResult = refreshTokenService.validRefreshTokenExists(user)
            ? null
            : refreshTokenService.create(user);

        return new GenerateTokenResponse(
            accessToken,
            tokenResponse.token_type(),
            refreshTokenResult != null ? refreshTokenResult.plainToken() : null,
            tokenResponse.expires_in(),
            tokenResponse.scope(),
            new SessionInfo(user.getTenantId(), user.getRole().name()),
            new UserInfo(claims.getStringClaim("email"), claims.getStringClaim("name"))
        );
    }

    @Transactional
    public GenerateTokenResponse refresh(String refreshTokenPlain) {
        RefreshToken stored = refreshTokenService.validate(refreshTokenPlain);
        TenantUser user = tenantUserService.requireActiveUserById(stored.getTenantId(), stored.getUserId());

        String newAccessToken = jwtService.generateToken(
            user.getUserId(),
            user.getTenantId(),
            user.getRole().name()
        );

        RefreshTokenResult rotated = refreshTokenService.rotate(stored);

        return new GenerateTokenResponse(
            newAccessToken,
            "Bearer",
            rotated.plainToken(), // new plain token
            jwtConfig.getAccessTokenLifetimeSec(),
            "openid email profile",
            new SessionInfo(user.getTenantId(), user.getRole().name()),
            null
        );
    }


    private JWTClaimsSet verifyGoogleToken(String idToken, String expectedNonce) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(googleConfig.getJwkSetUri()));

        ConfigurableJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource));

        JWTClaimsSet claims = processor.process(signedJWT, null);
        validateGoogleClaims(claims, expectedNonce);
        return claims;
    }

    private GoogleTokenResponse exchangeAuthorizationCode(String code) {
        GoogleTokenRequest tokenRequest = new GoogleTokenRequest(
            code,
            googleConfig.getClientId(),
            googleConfig.getClientSecret(),
            googleConfig.getRedirectUri(),
            "authorization_code"
        );

        WebClient webClient = WebClient.create(googleConfig.getTokenUri());
        GoogleTokenResponse tokenResponse = webClient.post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(tokenRequest.toFormData()))
            .retrieve()
            .bodyToMono(GoogleTokenResponse.class)
            .block();

        if (tokenResponse == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google token exchange returned no data");
        }

        return tokenResponse;
    }

    private void validateTokenResponse(GoogleTokenResponse tokenResponse) {
        Set<ConstraintViolation<GoogleTokenResponse>> violations = validator.validate(tokenResponse);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining(", "));
            throw new InvalidExternalClaimException(InvalidExternalClaimException.INVALID_GOOGLE_TOKEN_RES + msg);
        }

        if (tokenResponse.id_token() == null || tokenResponse.id_token().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google token response missing id_token");
        }
    }

    private void validateGoogleClaims(JWTClaimsSet claims, String expectedNonce) throws ResponseStatusException {
        if (!googleConfig.getIssuerUri().equals(claims.getIssuer())
            || !claims.getAudience().contains(googleConfig.getClientId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token");
        }

        Instant now = Instant.now();
        Date exp = claims.getExpirationTime();
        if (exp == null || exp.toInstant().isBefore(now.minusSeconds(CLOCK_SKEW_SECONDS))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID token expired");
        }

        Date nbf = claims.getNotBeforeTime();
        if (nbf != null && nbf.toInstant().isAfter(now.plusSeconds(CLOCK_SKEW_SECONDS))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID token not yet valid");
        }

        Date iat = claims.getIssueTime();
        if (iat != null && iat.toInstant().isAfter(now.plusSeconds(CLOCK_SKEW_SECONDS))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID token issue time invalid");
        }

        try {
            Boolean emailVerified = claims.getBooleanClaim("email_verified");
            if (emailVerified == null || !emailVerified) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google account email not verified");
            }

            String nonceClaim = claims.getStringClaim("nonce");
            if (expectedNonce != null && !expectedNonce.equals(nonceClaim)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid nonce");
            }
        } catch (java.text.ParseException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token claims", ex);
        }
    }
}
