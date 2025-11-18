package org.ashkan.ghaffari.inferenceanalyzer.dynamodb;

import org.ashkan.ghaffari.ingestor.dynamo.flaggedmessage.FlaggedMessage;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

@Repository
public class FlaggedMessageRepository {

    private final DynamoDbTable<FlaggedMessage> table;

    public FlaggedMessageRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("FlaggedMessage",
            TableSchema.fromBean(FlaggedMessage.class));
    }

    public List<FlaggedMessage> findUnanalyzed() {
        Expression filter = Expression.builder()
            .expression("analyzed = :v")
            .expressionValues(
                Map.of(":v", AttributeValue.builder().bool(false).build())
            )
            .build();

        PageIterable<FlaggedMessage> pages = table.scan(r -> r.filterExpression(filter));

        return pages.items().stream().toList();
    }
}