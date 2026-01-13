package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.dto.response.FraudAnalysisResponse;
import org.ashkan.ghaffari.adminconsole.service.FraudAnalysisAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tenant/{tenantId}/analysis")
public class FraudAnalysisController {

    private final FraudAnalysisAdminService fraudAnalysisAdminService;

    public FraudAnalysisController(FraudAnalysisAdminService fraudAnalysisAdminService) {
        this.fraudAnalysisAdminService = fraudAnalysisAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','ANALYST','VIEWER') and @tenantSecurity.canAccessTenant(#tenantId)")
    public ResponseEntity<List<FraudAnalysisResponse>> list(@PathVariable String tenantId) {
        return ResponseEntity.ok(fraudAnalysisAdminService.listByTenant(tenantId));
    }

    @GetMapping("/{analysisId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','ANALYST','VIEWER') and @tenantSecurity.canAccessTenant(#tenantId)")
    public ResponseEntity<FraudAnalysisResponse> get(@PathVariable String tenantId,
                                                     @PathVariable String analysisId) {
        return ResponseEntity.ok(fraudAnalysisAdminService.get(tenantId, analysisId));
    }
}
