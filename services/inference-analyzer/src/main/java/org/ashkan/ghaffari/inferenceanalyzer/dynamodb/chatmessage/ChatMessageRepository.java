package org.ashkan.ghaffari.inferenceanalyzer.dynamodb.chatmessage;

import org.ashkan.ghaffari.common.dynamo.chatmessage.ChatMessage;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Collections;
import java.util.List;

@Repository
public class ChatMessageRepository {

    private final DynamoDbTable<ChatMessage> table;

    public ChatMessageRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("ChatMessage",
            TableSchema.fromBean(ChatMessage.class));
    }

    public List<ChatMessage> findBefore(String chatId, long ts, int limit) {
        QueryConditional beforeCond = QueryConditional.sortLessThan(
            Key.builder()
                .partitionValue(chatId)
                .sortValue(ts)
                .build()
        );

        // We query DESCENDING, then reverse
        PageIterable<ChatMessage> pages = table.query(r -> r
            .queryConditional(beforeCond)
            .scanIndexForward(false)  // newest first
            .limit(limit)
        );

        List<ChatMessage> items = pages.items().stream().toList();

        // Put in ascending order so window is chronological
        Collections.reverse(items);

        return items;
    }

    public List<ChatMessage> findAfter(String chatId, long ts, int limit) {
        QueryConditional afterCond = QueryConditional.sortGreaterThan(
            Key.builder()
                .partitionValue(chatId)
                .sortValue(ts)
                .build()
        );

        // Query ASCENDING because we want oldest-after-first
        PageIterable<ChatMessage> pages = table.query(r -> r
            .queryConditional(afterCond)
            .scanIndexForward(true)   // chronological
            .limit(limit)
        );

        return pages.items().stream().toList();
    }


}