package com.sjy.LitHub.post.cache.post;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostDetailCacheManager {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PostDetailCacheManager(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public PostDetailResponseDTO getOrPut(String key, Supplier<PostDetailResponseDTO> dbFetcher) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached != null) {
				return objectMapper.readValue(cached, PostDetailResponseDTO.class);
			}
			PostDetailResponseDTO result = dbFetcher.get();
			savePostDetail(key, result, CachePolicy.POST_DETAIL_NON_POPULAR.getTtl());
			return result;
		} catch (Exception e) {
			log.warn("Redis getOrPut 실패 - key: {}, error: {}", key, e.getMessage());
			return dbFetcher.get();
		}
	}

	public void savePostDetail(String key, PostDetailResponseDTO dto) {
		savePostDetail(key, dto, CachePolicy.POST_DETAIL.getTtl());
	}

	private void savePostDetail(String key, PostDetailResponseDTO dto, Duration ttl) {
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, ttl);
		} catch (Exception e) {
			log.warn("Redis 캐시 저장 실패 - key: {}, error: {}", key, e.getMessage());
		}
	} // 게시글 상세 데이터를 Redis 저장 (전체 저장)

	public void deletePostDetail(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.warn("Redis 캐시 삭제 실패 - key: {}, error: {}", key, e.getMessage());
		}
	}
}