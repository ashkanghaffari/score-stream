package org.ashkan.ghaffari.common.dynamo.fraudanalysis;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FraudAnalysis {

    private String analysisId;
    private Long timestamp;
    private String flaggedMessageId;
    private boolean scamLikely;
    private String threatLevel;
    private String scammerUserId;
    private String victimUserId;
    private String scamType;
    private String summary;
    private String recommendation;
    private String modelName;
    private String responseId;
    private String tenantId;
    private String appId;

    public FraudAnalysis() {}

    public FraudAnalysis(String analysisId, Long timestamp, String flaggedMessageId,
                         boolean scamLikely, String threatLevel, String scammerUserId,
                         String victimUserId, String scamType, String summary,
                         String recommendation, String modelName, String responseId,
                         String tenantId, String appId) {
        this.analysisId = analysisId;
        this.timestamp = timestamp;
        this.flaggedMessageId = flaggedMessageId;
        this.scamLikely = scamLikely;
        this.threatLevel = threatLevel;
        this.scammerUserId = scammerUserId;
        this.victimUserId = victimUserId;
        this.scamType = scamType;
        this.summary = summary;
        this.recommendation = recommendation;
        this.modelName = modelName;
        this.responseId = responseId;
        this.tenantId = tenantId;
        this.appId = appId;
    }

    @DynamoDbPartitionKey
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }

    @DynamoDbSortKey
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getFlaggedMessageId() { return flaggedMessageId; }
    public void setFlaggedMessageId(String flaggedMessageId) { this.flaggedMessageId = flaggedMessageId; }

    public boolean isScamLikely() { return scamLikely; }
    public void setScamLikely(boolean scamLikely) { this.scamLikely = scamLikely; }

    public String getThreatLevel() { return threatLevel; }
    public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }

    public String getScammerUserId() { return scammerUserId; }
    public void setScammerUserId(String scammerUserId) { this.scammerUserId = scammerUserId; }

    public String getVictimUserId() { return victimUserId; }
    public void setVictimUserId(String victimUserId) { this.victimUserId = victimUserId; }

    public String getScamType() { return scamType; }
    public void setScamType(String scamType) { this.scamType = scamType; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
}
