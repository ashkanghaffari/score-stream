package org.ashkan.ghaffari.common.dynamo.flaggedmessage;

import org.ashkan.ghaffari.common.dynamo.chatmessage.MapAttributeConverter;
import org.ashkan.ghaffari.common.ruleengine.RuleResult;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;
import java.util.Map;

@DynamoDbBean
public class FlaggedMessage {

    private String chatId;
    private Long timestamp;
    private String messageId;
    private String senderId;
    private Map<String, Object> payload;
    private int totalScore;
    private String decision;
    private List<RuleResult> triggeredRules;
    private boolean analyzed;
    private String analysisId;

    public FlaggedMessage() {}

    public FlaggedMessage(String chatId, Long timestamp, String messageId, String senderId, Map<String, Object> payload,
                          int totalScore, String decision, List<RuleResult> triggeredRules, boolean analyzed,
                          String analysisId) {
        this.chatId = chatId;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.senderId = senderId;
        this.payload = payload;
        this.totalScore = totalScore;
        this.decision = decision;
        this.triggeredRules = triggeredRules;
        this.analyzed = analyzed;
        this.analysisId = analysisId;
    }

    @DynamoDbPartitionKey
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    @DynamoDbSortKey
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    @DynamoDbConvertedBy(MapAttributeConverter.class)
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    @DynamoDbConvertedBy(RuleResultListConverter.class)
    public List<RuleResult> getTriggeredRules() { return triggeredRules; }
    public void setTriggeredRules(List<RuleResult> triggeredRules) { this.triggeredRules = triggeredRules; }

    public boolean isAnalyzed() { return analyzed; }
    public void setAnalyzed(boolean analyzed) { this.analyzed = analyzed; }

    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
}
