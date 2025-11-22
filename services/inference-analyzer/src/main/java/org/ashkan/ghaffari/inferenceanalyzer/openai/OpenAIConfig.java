package org.ashkan.ghaffari.inferenceanalyzer.openai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class OpenAIConfig {

    @Bean
    public WebClient openAiWebClient(WebClient.Builder builder, OpenAIProperties properties) {
        return builder
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(512 * 1024))
                    .build()
            )
            .build();
    }
}
