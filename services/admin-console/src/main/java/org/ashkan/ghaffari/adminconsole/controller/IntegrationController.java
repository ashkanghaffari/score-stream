package org.ashkan.ghaffari.adminconsole.controller;

import jakarta.validation.Valid;
import org.ashkan.ghaffari.adminconsole.dto.request.CreateIntegrationRequest;
import org.ashkan.ghaffari.adminconsole.dto.request.UpdateIntegrationStatusRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.IntegrationResponse;
import org.ashkan.ghaffari.adminconsole.service.IntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant/{tenantId}/integration")
public class IntegrationController {
    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping
    public ResponseEntity<IntegrationResponse> create(@PathVariable String tenantId,
                                                      @Valid @RequestBody CreateIntegrationRequest request) {
        IntegrationResponse response = integrationService.create(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IntegrationResponse>> list(@PathVariable String tenantId) {
        return ResponseEntity.ok(integrationService.list(tenantId));
    }

    @GetMapping("/{integrationId}")
    public ResponseEntity<IntegrationResponse> get(@PathVariable String tenantId,
                                                   @PathVariable String integrationId) {
        return ResponseEntity.ok(integrationService.get(tenantId, integrationId));
    }

    @PatchMapping("/{integrationId}/status")
    public ResponseEntity<IntegrationResponse> updateStatus(@PathVariable String tenantId,
                                                            @PathVariable String integrationId,
                                                            @Valid @RequestBody UpdateIntegrationStatusRequest request) {
        return ResponseEntity.ok(integrationService.updateStatus(tenantId, integrationId, request));
    }
}
