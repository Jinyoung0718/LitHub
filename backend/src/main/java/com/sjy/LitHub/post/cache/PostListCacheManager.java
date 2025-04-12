package com.sjy.LitHub.post.cache;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.model.res.PostSummaryResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PostListCacheManager {

	private static final Duration TTL = Duration.ofMinutes(5);

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PostListCacheManager(
		@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper
	) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public Page<PostSummaryResponseDTO> getOrPut(String key, Supplier<Page<PostSummaryResponseDTO>> dbFetcher) {
		Page<PostSummaryResponseDTO> result = get(key);
		if (result != null) return result;

		result = dbFetcher.get();
		save(key, result, TTL);
		return result;
	}

	public void save(String key, Page<PostSummaryResponseDTO> page, Duration ttl) {
		try {
			String json = objectMapper.writeValueAsString(page);
			redisTemplate.opsForValue().set(key, json, ttl);
			log.debug("[캐시 저장 완료] key: {}", key);
		} catch (Exception e) {
			log.warn("[캐시 저장 실패] key: {}, error: {}", key, e.getMessage());
		}
	}

	private Page<PostSummaryResponseDTO> get(String key) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached == null) {
				log.debug("[캐시 MISS] key: {}", key);
				return null;
			}

			JavaType type = objectMapper.getTypeFactory()
				.constructParametricType(PageImpl.class, PostSummaryResponseDTO.class);
			return objectMapper.readValue(cached, type);
		} catch (Exception e) {
			log.warn("[캐시 조회 실패] key: {}, error: {}", key, e.getMessage());
			return null;
		}
	}
}