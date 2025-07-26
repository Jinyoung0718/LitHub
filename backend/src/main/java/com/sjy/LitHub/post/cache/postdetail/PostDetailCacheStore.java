package com.sjy.LitHub.post.cache.postdetail;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostDetailCacheStore {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PostDetailCacheStore(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public PostDetailResponseDTO get(String key) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached == null) return null;
			return objectMapper.readValue(cached, PostDetailResponseDTO.class);
		} catch (Exception e) {
			log.warn("Redis 캐시 조회 실패 - key: {}, error: {}", key, e.getMessage());
			return null;
		}
	}

	public void put(String key, PostDetailResponseDTO dto, Duration ttl) {
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, ttl);
			log.debug("Redis 캐시 저장 - key: {}", key);
		} catch (Exception e) {
			log.warn("Redis 캐시 저장 실패 - key: {}, error: {}", key, e.getMessage());
		}
	}

	public void delete(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.warn("Redis 캐시 삭제 실패 - key: {}, error: {}", key, e.getMessage());
		}
	}
}