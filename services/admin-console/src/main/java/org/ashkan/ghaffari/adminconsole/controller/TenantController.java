package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantRequest;
import org.ashkan.ghaffari.adminconsole.dto.request.UpdateTenantStatusRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantResponse;
import org.ashkan.ghaffari.adminconsole.service.TenantLookupService;
import org.ashkan.ghaffari.adminconsole.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/tenant")
public class TenantController {
    private static final String ROOT_TENANT_NAME = "root-tenant";
    private final TenantService tenantService;
    private final TenantLookupService tenantLookupService;

    public TenantController(TenantService tenantService, TenantLookupService tenantLookupService) {
        this.tenantService = tenantService;
        this.tenantLookupService = tenantLookupService;
    }

    @GetMapping("/{tenantName}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable String tenantName) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        TenantResponse response = tenantService.getTenant(tenantId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<TenantResponse> createTenant(
        @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<TenantResponse>> getTenants() {
        List<TenantResponse> response = tenantService.getTenants();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tenantName}/status")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<TenantResponse> changeStatus(
        @PathVariable String tenantName,
        @Valid @RequestBody UpdateTenantStatusRequest request) {
        if (ROOT_TENANT_NAME.equals(tenantName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Root tenant status cannot be changed");
        }
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        TenantResponse response = tenantService.changeStatus(tenantId, request.status());
        return ResponseEntity.ok(response);
    }

}
