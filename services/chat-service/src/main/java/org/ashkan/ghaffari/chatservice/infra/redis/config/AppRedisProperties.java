package org.ashkan.ghaffari.chatservice.infra.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.redis")
public record AppRedisProperties(
    Idempotency idempotency
) {
    public record Idempotency(
        String prefix,
        Duration ttl
    ) {}
}
