package com.sjy.LitHub.post.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.cache.util.PostUpdatePart;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostCacheManager {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PostCacheManager(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

	public void savePostDetail(String key, PostDetailResponseDTO dto) {
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, DEFAULT_TTL);
		} catch (Exception e) {
			log.warn("Redis 캐시 저장 실패 - key: {}, error: {}", key, e.getMessage());
		}
	} // 게시글 상세 데이터를 Redis 저장 (전체 저장)

	public Optional<PostDetailResponseDTO> getPostDetail(String key) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached == null) return Optional.empty();
			return Optional.of(objectMapper.readValue(cached, PostDetailResponseDTO.class));
		} catch (Exception e) {
			log.warn("Redis 캐시 조회 실패 - key: {}, error: {}", key, e.getMessage());
			return Optional.empty();
		}
	} // Redis 에서 해당 키의 데이터를 읽어서 DTO 변환

	public void deletePostDetail(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.warn("Redis 캐시 삭제 실패 - key: {}, error: {}", key, e.getMessage());
		}
	}

	public <T> void updatePostDetailField(String key, PostUpdatePart part, T data) {
		try {
			Optional<PostDetailResponseDTO> cache = getPostDetail(key);
			if (cache.isPresent()) {
				part.apply(cache.get(), data);
				savePostDetail(key, cache.get());
			}
		} catch (Exception e) {
			log.warn("Redis 부분 캐시 업데이트 실패 - key: {}, part: {}, error: {}", key, part.name(), e.getMessage());
		}
	} // 캐시된 DTO 의 일부 필드만 수정하고 다시 저장 [수정 부분 -> ENUM 화]
}