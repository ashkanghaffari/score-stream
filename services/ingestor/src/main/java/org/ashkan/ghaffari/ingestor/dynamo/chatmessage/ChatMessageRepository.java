package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class ChatMessageRepository {

    private final DynamoDbTable<ChatMessage> table;

    public ChatMessageRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("ChatMessages",
            TableSchema.fromBean(ChatMessage.class));
    }

    public void save(ChatMessage msg) {
        table.putItem(msg);
    }

}