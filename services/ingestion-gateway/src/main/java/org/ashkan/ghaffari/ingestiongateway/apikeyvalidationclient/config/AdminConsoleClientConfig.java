package org.ashkan.ghaffari.ingestiongateway.apikeyvalidationclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AdminConsoleClientConfig {

    @Bean
    public WebClient adminConsoleWebClient(
        WebClient.Builder builder,
        AdminConsoleClientProperties props
    ) {
        return builder
            .baseUrl(props.baseUrl())
            .defaultHeader("X-Internal-Token", props.token())
            .build();
    }
}
