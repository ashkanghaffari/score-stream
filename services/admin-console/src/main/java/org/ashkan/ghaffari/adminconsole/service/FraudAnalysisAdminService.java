package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.response.FraudAnalysisResponse;
import org.ashkan.ghaffari.adminconsole.repository.FraudAnalysisAdminRepository;
import org.ashkan.ghaffari.common.dynamo.fraudanalysis.FraudAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class FraudAnalysisAdminService {

    private final FraudAnalysisAdminRepository repository;

    public FraudAnalysisAdminService(FraudAnalysisAdminRepository repository) {
        this.repository = repository;
    }

    public FraudAnalysisResponse get(String tenantId, String analysisId) {
        FraudAnalysis analysis = repository.find(analysisId);
        if (analysis == null || !tenantId.equals(analysis.getTenantId())) {
            throw new ResponseStatusException(NOT_FOUND, "Fraud analysis not found: " + analysisId);
        }
        return toResponse(analysis);
    }

    public List<FraudAnalysisResponse> listByTenant(String tenantId) {
        return repository.findByTenant(tenantId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private FraudAnalysisResponse toResponse(FraudAnalysis analysis) {
        return new FraudAnalysisResponse(
            analysis.getAnalysisId(),
            analysis.getTenantId(),
            analysis.getAppId(),
            analysis.getFlaggedMessageId(),
            analysis.isScamLikely(),
            analysis.getThreatLevel(),
            analysis.getScammerUserId(),
            analysis.getVictimUserId(),
            analysis.getScamType(),
            analysis.getSummary(),
            analysis.getRecommendation(),
            analysis.getModelName(),
            analysis.getResponseId(),
            analysis.getTimestamp()
        );
    }
}
