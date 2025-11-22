package org.ashkan.ghaffari.inferenceanalyzer.openai;

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
