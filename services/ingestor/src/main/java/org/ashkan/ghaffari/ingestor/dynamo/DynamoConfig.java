package org.ashkan.ghaffari.ingestor.dynamo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
public class DynamoConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(
        @Value("${dynamodb.endpoint:}") String endpoint,
        @Value("${dynamodb.region:us-west-2}") String region) {

        DynamoDbClientBuilder builder = DynamoDbClient.builder()
            .region(Region.of(region));

        if (!endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")
                    )
                );
        }

        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build();
    }
}
