package com.everjoy.aigateway.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class QuotaService {
    private static final long DEFAULT_DAILY_LIMIT = 1000;
    private final StringRedisTemplate redisTemplate;

    public QuotaService(Optional<StringRedisTemplate> redisTemplate) {
        this.redisTemplate = redisTemplate.orElse(null);
    }

    public boolean tryAcquire(String tenantId) {
        if (redisTemplate == null) {
            return true;
        }
        String key = "quota:" + tenantId + ":" + LocalDate.now();
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redisTemplate.expire(key, Duration.ofDays(2));
            }
            return count == null || count <= DEFAULT_DAILY_LIMIT;
        } catch (Exception ex) {
            return true;
        }
    }
}
