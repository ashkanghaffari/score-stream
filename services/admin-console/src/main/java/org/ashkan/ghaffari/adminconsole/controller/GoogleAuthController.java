package org.ashkan.ghaffari.adminconsole.controller;

import jakarta.servlet.http.HttpSession;
import org.ashkan.ghaffari.adminconsole.dto.request.RefreshTokenRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.GenerateTokenResponse;
import org.ashkan.ghaffari.adminconsole.security.config.properties.GoogleIdpProperties;
import org.ashkan.ghaffari.adminconsole.service.GoogleAuthService;
import org.ashkan.ghaffari.adminconsole.service.TenantLookupService;
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

    private final GoogleIdpProperties googleIdpProperties;

    private final TenantLookupService tenantLookupService;

    public GoogleAuthController(GoogleAuthService googleAuthService,
                                GoogleIdpProperties googleIdpProperties,
                                TenantLookupService tenantLookupService) {
        this.googleAuthService = googleAuthService;
        this.googleIdpProperties = googleIdpProperties;
        this.tenantLookupService = tenantLookupService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("tenantName") String tenantName, HttpSession session) {
        if (tenantName == null || tenantName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing tenantName");
        }

        String tenantId = tenantLookupService.requireTenantId(tenantName);
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        session.setMaxInactiveInterval(300); // limit lifetime of OAuth handshake data
        session.setAttribute("oauth_state", state);
        session.setAttribute("oauth_nonce", nonce);
        session.setAttribute("oauth_tenant_id", tenantId);

        String authUrl = googleIdpProperties.getAuthorizationUri()
            + "?response_type=code"
            + "&client_id=" + googleIdpProperties.getClientId()
            + "&redirect_uri=" + googleIdpProperties.getRedirectUri()
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
