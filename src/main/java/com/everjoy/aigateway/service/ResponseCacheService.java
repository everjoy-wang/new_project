package com.everjoy.aigateway.service;

import com.everjoy.aigateway.model.ChatResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResponseCacheService {
    private static final Duration TTL = Duration.ofMinutes(10);
    private final StringRedisTemplate redisTemplate;

    public ResponseCacheService(Optional<StringRedisTemplate> redisTemplate) {
        this.redisTemplate = redisTemplate.orElse(null);
    }

    public Optional<ChatResponse> get(String cacheKey) {
        if (redisTemplate == null) {
            return Optional.empty();
        }
        try {
            String value = redisTemplate.opsForValue().get(cacheKey);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(new ChatResponse(
                    UUID.randomUUID().toString(),
                    "cache",
                    value,
                    Math.max(1, value.length() / 4),
                    true,
                    Instant.now()
            ));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public void put(String cacheKey, String content) {
        if (redisTemplate == null) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(cacheKey, content, TTL);
        } catch (Exception ignored) {
            // Fail open if Redis is unavailable.
        }
    }
}
