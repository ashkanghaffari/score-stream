package org.ashkan.ghaffari.common.dynamo.chatmessage;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Map;

@DynamoDbBean
public class ChatMessage {

    private String tenantId;
    private String appId;
    private String chatScopeKey;
    private String chatId;
    private Long timestamp;
    private String messageId;
    private String senderId;
    private Map<String, Object> payload;

    public ChatMessage() {}

    public ChatMessage(String tenantId, String appId, String chatId, String chatScopeKey,
                       Long timestamp, String messageId, String senderId,
                       Map<String, Object> payload) {
        this.tenantId = tenantId;
        this.appId = appId;
        this.chatScopeKey = chatScopeKey;
        this.chatId = chatId;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.senderId = senderId;
        this.payload = payload;
    }

    @DynamoDbPartitionKey
    public String getChatScopeKey() { return chatScopeKey; }
    public void setChatScopeKey(String chatScopeKey) { this.chatScopeKey = chatScopeKey; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

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
}
