package org.ashkan.ghaffari.inferenceanalyzer.openai.dto.response;

public record FraudAnalysisResult(
    String analysisId,
    boolean scamLikely,
    String threatLevel,
    String scammerUserId,
    String victimUserId,
    String scamType,
    String summary,
    String recommendation
) {}
