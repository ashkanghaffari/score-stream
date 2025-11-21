package org.ashkan.ghaffari.ingestor.dynamo.flaggedmessage;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class FlaggedMessageRepository {

    private final DynamoDbTable<FlaggedMessage> table;

    public FlaggedMessageRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("FlaggedMessage",
            TableSchema.fromBean(FlaggedMessage.class));
    }

    public void save(FlaggedMessage message) {
        table.putItem(message);
    }
}
