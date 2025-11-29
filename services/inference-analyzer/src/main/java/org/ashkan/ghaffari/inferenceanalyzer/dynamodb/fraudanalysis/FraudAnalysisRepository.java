package org.ashkan.ghaffari.inferenceanalyzer.dynamodb.fraudanalysis;

import org.ashkan.ghaffari.common.dynamo.fraudanalysis.FraudAnalysis;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class FraudAnalysisRepository {

    private final DynamoDbTable<FraudAnalysis> table;

    public FraudAnalysisRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("FraudAnalysis",
            TableSchema.fromBean(FraudAnalysis.class));
    }

    public void save(FraudAnalysis message) { table.putItem(message); }
}
