package org.ashkan.ghaffari.adminconsole.controller;

import jakarta.validation.Valid;
import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantUserRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantUserResponse;
import org.ashkan.ghaffari.adminconsole.service.TenantLookupService;
import org.ashkan.ghaffari.adminconsole.service.TenantUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/tenant/{tenantName}/user")
public class TenantUserController {
    private final TenantUserService tenantUserService;
    private final TenantLookupService tenantLookupService;

    public TenantUserController(TenantUserService tenantUserService, TenantLookupService tenantLookupService) {
        this.tenantUserService = tenantUserService;
        this.tenantLookupService = tenantLookupService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<TenantUserResponse> addUser(@PathVariable String tenantName,
                                                      @Valid @RequestBody CreateTenantUserRequest request) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        TenantUserResponse response = tenantUserService.addUser(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<TenantUserResponse> inviteUser(@PathVariable String tenantName,
                                                         @Valid @RequestBody CreateTenantUserRequest request) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        TenantUserResponse response = tenantUserService.inviteUser(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<TenantUserResponse> getUser(@PathVariable String tenantName,
                                                      @PathVariable String userId) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        TenantUserResponse response = tenantUserService.getUser(tenantId, userId);
        return ResponseEntity.ok(response);
    }
}
