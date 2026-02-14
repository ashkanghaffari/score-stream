package org.ashkan.ghaffari.chatservice.infra.redis.repository;

import org.ashkan.ghaffari.chatservice.chat.ws.idempotency.MessageIdempotencyId;
import org.ashkan.ghaffari.chatservice.infra.redis.config.AppRedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MessageIdempotencyRepository {
    private static final String IDEMPOTENCY_MARKER = "1";

    private final RedisTemplate<String, String> redisTemplate;
    private final AppRedisProperties appRedisProperties;
    private final String keyPrefix;
    private final Duration ttl;

    public MessageIdempotencyRepository(RedisTemplate<String,
                                        String> redisTemplate,
                                        AppRedisProperties appRedisProperties) {
        this.redisTemplate = redisTemplate;
        this.appRedisProperties = appRedisProperties;
        this.keyPrefix = appRedisProperties.idempotency().prefix() + ":";
        this.ttl = appRedisProperties.idempotency().ttl();
    }

    public boolean tryStore(MessageIdempotencyId key) {
        Boolean success = redisTemplate
            .opsForValue()
            .setIfAbsent(generateKey(key),
                IDEMPOTENCY_MARKER, ttl);
        return Boolean.TRUE.equals(success);
    }

    private String generateKey(MessageIdempotencyId key) {
        return keyPrefix + key.value();
    }
}
