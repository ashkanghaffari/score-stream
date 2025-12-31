package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.common.dynamo.fraudanalysis.FraudAnalysis;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;

@Repository
public class FraudAnalysisAdminRepository {

    private final DynamoDbTable<FraudAnalysis> table;

    public FraudAnalysisAdminRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("FraudAnalysis", TableSchema.fromBean(FraudAnalysis.class));
    }

    public FraudAnalysis find(String analysisId) {
        return table.getItem(
            Key.builder()
                .partitionValue(analysisId)
                .build()
        );
    }

    public List<FraudAnalysis> findByTenant(String tenantId) {
        Expression filter = Expression.builder()
            .expression("tenantId = :t")
            .expressionValues(java.util.Map.of(
                ":t", AttributeValue.builder().s(tenantId).build()
            ))
            .build();

        PageIterable<FraudAnalysis> pages = table.scan(r -> r.filterExpression(filter));
        return pages.items().stream().toList();
    }
}
