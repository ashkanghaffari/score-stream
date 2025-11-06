package org.ashkan.ghaffari.ingestor.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(10);

    public IdempotencyRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryStore(String key) {
        Boolean success = redisTemplate
            .opsForValue()
            .setIfAbsent(key, "1", TTL);
        return Boolean.TRUE.equals(success);
    }
}
