package org.ashkan.ghaffari.adminconsole.controller;

import jakarta.validation.Valid;
import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantUserRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantUserResponse;
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
@RequestMapping("/v1/tenant/{tenantId}/user")
public class TenantUserController {
    private final TenantUserService tenantUserService;

    public TenantUserController(TenantUserService tenantUserService) {
        this.tenantUserService = tenantUserService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<TenantUserResponse> addUser(@PathVariable String tenantId,
                                                      @Valid @RequestBody CreateTenantUserRequest request) {
        TenantUserResponse response = tenantUserService.addUser(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<TenantUserResponse> inviteUser(@PathVariable String tenantId,
                                                         @Valid @RequestBody CreateTenantUserRequest request) {
        TenantUserResponse response = tenantUserService.inviteUser(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<TenantUserResponse> getUser(@PathVariable String tenantId,
                                                      @PathVariable String userId) {
        TenantUserResponse response = tenantUserService.getUser(tenantId, userId);
        return ResponseEntity.ok(response);
    }
}
