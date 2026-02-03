package org.ashkan.ghaffari.adminconsole.controller;

import jakarta.validation.Valid;
import org.ashkan.ghaffari.adminconsole.dto.request.CreateIntegrationRequest;
import org.ashkan.ghaffari.adminconsole.dto.request.UpdateIntegrationStatusRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.CreateIntegrationApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.IntegrationResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.IntegrationApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.service.IntegrationApiKeyService;
import org.ashkan.ghaffari.adminconsole.service.IntegrationService;
import org.ashkan.ghaffari.adminconsole.service.TenantLookupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant/{tenantName}/integration")
public class IntegrationController {
    private final IntegrationService integrationService;
    private final IntegrationApiKeyService integrationApiKeyService;
    private final TenantLookupService tenantLookupService;

    public IntegrationController(IntegrationService integrationService,
                                 IntegrationApiKeyService integrationApiKeyService,
                                 TenantLookupService tenantLookupService) {
        this.integrationService = integrationService;
        this.integrationApiKeyService = integrationApiKeyService;
        this.tenantLookupService = tenantLookupService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<IntegrationResponse> create(@PathVariable String tenantName,
                                                      @Valid @RequestBody CreateIntegrationRequest request) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        IntegrationResponse response = integrationService.create(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<List<IntegrationResponse>> list(@PathVariable String tenantName) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        return ResponseEntity.ok(integrationService.list(tenantId));
    }

    @GetMapping("/{integrationId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<IntegrationResponse> get(@PathVariable String tenantName,
                                                   @PathVariable String integrationId) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        return ResponseEntity.ok(integrationService.get(tenantId, integrationId));
    }

    @PatchMapping("/{integrationId}/status")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<IntegrationResponse> updateStatus(@PathVariable String tenantName,
                                                            @PathVariable String integrationId,
                                                            @Valid @RequestBody UpdateIntegrationStatusRequest request) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        return ResponseEntity.ok(integrationService.updateStatus(tenantId, integrationId, request));
    }

    @PostMapping("/{integrationId}/api-keys")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<CreateIntegrationApiKeyResponse> createApiKey(
        @PathVariable String tenantName,
        @PathVariable String integrationId) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        CreateIntegrationApiKeyResponse response = integrationApiKeyService.create(tenantId, integrationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{integrationId}/api-keys")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<List<IntegrationApiKeyResponse>> listApiKeys(@PathVariable String tenantName,
                                                                       @PathVariable String integrationId) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        return ResponseEntity.ok(integrationApiKeyService.list(tenantId, integrationId));
    }

    @DeleteMapping("/{integrationId}/api-keys/{apiKeyId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<Void> revokeApiKey(@PathVariable String tenantName,
                                             @PathVariable String integrationId,
                                             @PathVariable String apiKeyId) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        integrationApiKeyService.revoke(tenantId, integrationId, apiKeyId);
        return ResponseEntity.noContent().build();
    }
}
