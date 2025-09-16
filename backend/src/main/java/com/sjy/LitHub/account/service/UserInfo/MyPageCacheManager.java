package com.sjy.LitHub.account.service.UserInfo;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MyPageCacheManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public MyPageCacheManager(
        @Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
        ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> getCache(String key, Class<T> type) {
        try {
            String cachedValue = redisTemplate.opsForValue().get(key);
            if (cachedValue == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(cachedValue, type));
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 역직렬화 실패 - key: {}, type: {}", key, type.getSimpleName(), e);
            throw new InvalidRedisException(BaseResponseStatus.REDIS_DESERIALIZATION_FAILED);
        } catch (Exception e) {
            log.error("Redis 캐시 조회 실패 - key: {}, type: {}", key, type.getSimpleName(), e);
            throw new InvalidRedisException(BaseResponseStatus.REDIS_CACHE_UPDATE_FAILED);
        }
    }

    public <T> void putCache(String key, T data) {
        try {
            String jsonValue = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, jsonValue, Duration.ofHours(1));
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 직렬화 실패 - key: {}", key, e);
            throw new InvalidRedisException(BaseResponseStatus.REDIS_DESERIALIZATION_FAILED);
        } catch (Exception e) {
            log.error("Redis 캐시 저장 실패 - key: {}", key, e);
            throw new InvalidRedisException(BaseResponseStatus.REDIS_CACHE_UPDATE_FAILED);
        }
    }

    public void evictCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis 캐시 삭제 실패 - key: {}", key, e);
            throw new InvalidRedisException(BaseResponseStatus.REDIS_CACHE_UPDATE_FAILED);
        }
    }

    public <T> T getOrFetchAndPut(String key, Supplier<T> fetcher, Class<T> type) {
        return getCache(key, type)
            .orElseGet(() -> {
                T data = fetcher.get();
                putCache(key, data);
                return data;
            });
    } // 캐시에서 조회하고 없으면 DB/서비스에서 가져와 캐시에 저장 후 반환.
}