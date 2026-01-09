package org.ashkan.ghaffari.adminconsole.controller;

import jakarta.servlet.http.HttpSession;
import org.ashkan.ghaffari.adminconsole.dto.request.RefreshTokenRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.GenerateTokenResponse;
import org.ashkan.ghaffari.adminconsole.security.config.GoogleIdpConfig;
import org.ashkan.ghaffari.adminconsole.service.GoogleAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    private final GoogleIdpConfig googleIdpConfig;

    public GoogleAuthController(GoogleAuthService googleAuthService, GoogleIdpConfig googleIdpConfig) {
        this.googleAuthService = googleAuthService;
        this.googleIdpConfig = googleIdpConfig;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("tenantId") String tenantId, HttpSession session) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing tenantId");
        }

        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        session.setMaxInactiveInterval(300); // limit lifetime of OAuth handshake data
        session.setAttribute("oauth_state", state);
        session.setAttribute("oauth_nonce", nonce);
        session.setAttribute("oauth_tenant_id", tenantId);

        String authUrl = googleIdpConfig.getAuthorizationUri()
            + "?response_type=code"
            + "&client_id=" + googleIdpConfig.getClientId()
            + "&redirect_uri=" + googleIdpConfig.getRedirectUri()
            + "&scope=openid%20email%20profile"
            + "&access_type=offline"
            + "&prompt=consent"
            + "&state=" + state
            + "&nonce=" + nonce;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(authUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/callback")
    public ResponseEntity<GenerateTokenResponse> callback(
        @RequestParam("code") String code,
        @RequestParam("state") String state,
        HttpSession session) throws Exception {

        String expectedState = (String) session.getAttribute("oauth_state");
        if (!state.equals(expectedState)) {
            session.invalidate();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OAuth state");
        }

        String expectedNonce = (String) session.getAttribute("oauth_nonce");
        String expectedTenantId = (String) session.getAttribute("oauth_tenant_id");

        try {
            // clear one-time values before delegating
            session.removeAttribute("oauth_state");
            session.removeAttribute("oauth_nonce");
            session.removeAttribute("oauth_tenant_id");
            if (expectedNonce == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing OAuth nonce");
            }
            if (expectedTenantId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing OAuth tenant");
            }
            return ResponseEntity.ok(googleAuthService.callback(code, expectedNonce, expectedTenantId));
        } finally {
            session.invalidate();
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<GenerateTokenResponse> refresh(@RequestBody RefreshTokenRequest body) {
        String refreshToken = body.refreshToken();
        return ResponseEntity.ok(googleAuthService.refresh(refreshToken));
    }
}
