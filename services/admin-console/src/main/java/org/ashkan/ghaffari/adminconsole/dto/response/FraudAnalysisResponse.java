package org.ashkan.ghaffari.adminconsole.dto.response;

public record FraudAnalysisResponse(
    String analysisId,
    String tenantId,
    String appId,
    String flaggedMessageId,
    boolean scamLikely,
    String threatLevel,
    String scammerUserId,
    String victimUserId,
    String scamType,
    String summary,
    String recommendation,
    String modelName,
    String responseId,
    Long timestamp
) {}
