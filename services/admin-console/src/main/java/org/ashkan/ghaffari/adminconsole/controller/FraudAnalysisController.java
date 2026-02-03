package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.dto.response.FraudAnalysisResponse;
import org.ashkan.ghaffari.adminconsole.service.FraudAnalysisAdminService;
import org.ashkan.ghaffari.adminconsole.service.TenantLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant/{tenantName}/analysis")
public class FraudAnalysisController {

    private final FraudAnalysisAdminService fraudAnalysisAdminService;
    private final TenantLookupService tenantLookupService;

    public FraudAnalysisController(FraudAnalysisAdminService fraudAnalysisAdminService,
                                   TenantLookupService tenantLookupService) {
        this.fraudAnalysisAdminService = fraudAnalysisAdminService;
        this.tenantLookupService = tenantLookupService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','ANALYST','VIEWER') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<List<FraudAnalysisResponse>> list(@PathVariable String tenantName) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        return ResponseEntity.ok(fraudAnalysisAdminService.listByTenant(tenantId));
    }

    @GetMapping("/{analysisId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','ANALYST','VIEWER') and @tenantSecurity.canAccessTenantName(#tenantName)")
    public ResponseEntity<FraudAnalysisResponse> get(@PathVariable String tenantName,
                                                     @PathVariable String analysisId) {
        String tenantId = tenantLookupService.requireTenantId(tenantName);
        return ResponseEntity.ok(fraudAnalysisAdminService.get(tenantId, analysisId));
    }
}
