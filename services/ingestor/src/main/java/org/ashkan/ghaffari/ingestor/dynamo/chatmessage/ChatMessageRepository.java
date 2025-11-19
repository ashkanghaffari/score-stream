package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import org.ashkan.ghaffari.common.dynamo.chatmessage.ChatMessage;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class ChatMessageRepository {

    private final DynamoDbTable<ChatMessage> table;

    public ChatMessageRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("ChatMessage",
            TableSchema.fromBean(ChatMessage.class));
    }

    public void save(ChatMessage msg) {
        table.putItem(msg);
    }

}
