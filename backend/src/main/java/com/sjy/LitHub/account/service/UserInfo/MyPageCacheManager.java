package com.sjy.LitHub.account.service.UserInfo;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MyPageCacheManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public MyPageCacheManager(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> getCache(String key, Class<T> type) {
        try {
            String cachedValue = redisTemplate.opsForValue().get(key);
            if (cachedValue == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(cachedValue, type));
        } catch (Exception e) {
            log.warn("Redis 캐시 조회 실패 - key: {}, type: {}, error: {}", key, type.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    public <T> void putCache(String key, T data) {
        try {
            String jsonValue = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, jsonValue, Duration.ofHours(1));
        } catch (Exception e) {
            log.warn("Redis 캐시 저장 실패 - key: {}, error: {}", key, e.getMessage());
        }
    }

    public void evictCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis 캐시 삭제 실패 - key: {}, error: {}", key, e.getMessage());
        }
    }
}