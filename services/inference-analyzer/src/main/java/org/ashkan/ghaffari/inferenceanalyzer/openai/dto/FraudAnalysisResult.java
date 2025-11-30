package org.ashkan.ghaffari.inferenceanalyzer.openai.dto;

public record FraudAnalysisResult(
    String analysisId,
    boolean scamLikely,
    String threatLevel,
    String scammerUserId,
    String victimUserId,
    String scamType,
    String summary,
    String recommendation,
    String modelName,
    String responseId
) {}
