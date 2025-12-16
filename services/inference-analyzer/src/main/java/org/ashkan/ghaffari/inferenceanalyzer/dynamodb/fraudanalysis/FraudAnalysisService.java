package org.ashkan.ghaffari.inferenceanalyzer.dynamodb.fraudanalysis;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;
import org.ashkan.ghaffari.common.dynamo.fraudanalysis.FraudAnalysis;
import org.ashkan.ghaffari.inferenceanalyzer.openai.dto.FraudAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FraudAnalysisService {


    private static final Logger log = LoggerFactory.getLogger(FraudAnalysisService.class);
    private final FraudAnalysisRepository repository;

    public FraudAnalysisService(FraudAnalysisRepository repository) {
        this.repository = repository;
    }

    public void saveFraudAnalysis(FraudAnalysisResult fraudAnalysisResult, FlaggedMessage flaggedMessage) {
        FraudAnalysis fraudAnalysis = new FraudAnalysis(
            fraudAnalysisResult.analysisId(),
            Instant.now().toEpochMilli(),
            flaggedMessage.getMessageId(),
            fraudAnalysisResult.scamLikely(),
            fraudAnalysisResult.threatLevel(),
            fraudAnalysisResult.scammerUserId(),
            fraudAnalysisResult.victimUserId(),
            fraudAnalysisResult.scamType(),
            fraudAnalysisResult.summary(),
            fraudAnalysisResult.recommendation(),
            fraudAnalysisResult.modelName(),
            fraudAnalysisResult.responseId(),
            flaggedMessage.getTenantId(),
            flaggedMessage.getAppId()
        );

        repository.save(fraudAnalysis);
    }


}
