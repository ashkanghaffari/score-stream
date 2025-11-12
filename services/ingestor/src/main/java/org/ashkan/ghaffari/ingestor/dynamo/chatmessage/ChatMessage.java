package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Map;

@DynamoDbBean
public class ChatMessage {

    private String chatId;
    private Long timestamp;
    private String messageId;
    private String senderId;
    private Map<String, Object> payload;
    private boolean flagged;

    public ChatMessage() {}

    public ChatMessage(String chatId, Long timestamp, String messageId, String senderId,
                       Map<String, Object> payload, boolean flagged) {
        this.chatId = chatId;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.senderId = senderId;
        this.payload = payload;
        this.flagged = flagged;
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

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
}

