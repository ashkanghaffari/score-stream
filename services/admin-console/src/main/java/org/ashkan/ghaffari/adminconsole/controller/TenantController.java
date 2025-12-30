package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantRequest;
import org.ashkan.ghaffari.adminconsole.dto.request.UpdateTenantStatusRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantResponse;
import org.ashkan.ghaffari.adminconsole.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable String id) {
        TenantResponse response = tenantService.getTenant(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(
        @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getTenants() {
        List<TenantResponse> response = tenantService.getTenants();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TenantResponse> changeStatus(
        @PathVariable String id,
        @Valid @RequestBody UpdateTenantStatusRequest request) {
        TenantResponse response = tenantService.changeStatus(id, request.status());
        return ResponseEntity.ok(response);
    }

}
