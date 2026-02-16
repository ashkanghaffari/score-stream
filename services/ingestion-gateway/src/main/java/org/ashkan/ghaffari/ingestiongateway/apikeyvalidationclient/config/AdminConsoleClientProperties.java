package org.ashkan.ghaffari.ingestiongateway.apikeyvalidationclient.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin-console.client")
public record AdminConsoleClientProperties(
    String token,
    String baseUrl
) {}