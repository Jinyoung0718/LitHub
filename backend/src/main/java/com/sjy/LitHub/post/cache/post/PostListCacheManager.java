package com.sjy.LitHub.post.cache.post;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PostListCacheManager {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PostListCacheManager(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper
	) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public Page<PostSummaryResponseDTO> getOrPut(CachePolicy policy, Supplier<Page<PostSummaryResponseDTO>> dbFetcher, Object... args) {
		String key = policy.createKey(args);
		Page<PostSummaryResponseDTO> cached = get(key);
		if (cached != null) return cached;

		Page<PostSummaryResponseDTO> result = dbFetcher.get();
		save(key, result, policy.getTtl());
		return result;
	} // 캐시에서 데이터를 가져오고, 없으면 DB 조회 후 저장 (Lazy Caching)

	public void save(CachePolicy policy, Page<PostSummaryResponseDTO> page, Object... args) {
		save(policy.createKey(args), page, policy.getTtl());
	} // 정책 기반 키와 TTL 로 캐시 저장

	private void save(String key, Page<PostSummaryResponseDTO> page, Duration ttl) {
		try {
			String json = objectMapper.writeValueAsString(page);
			redisTemplate.opsForValue().set(key, json, ttl);
			log.debug("[캐시 저장 완료] key: {}", key);
		} catch (Exception e) {
			log.warn("[캐시 저장 실패] key: {}, error: {}", key, e.getMessage());
		}
	} // 주어진 key 에 Page 객체를 직렬화하여 Redis 에 TTL 과 함께 저장

	private Page<PostSummaryResponseDTO> get(String key) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached == null) return null;

			JavaType type = objectMapper.getTypeFactory()
				.constructParametricType(PageImpl.class, PostSummaryResponseDTO.class);
			return objectMapper.readValue(cached, type);
		} catch (Exception e) {
			log.warn("[캐시 조회 실패] key: {}, error: {}", key, e.getMessage());
			return null;
		}
	} // key 에 해당하는 Redis 값을 역직렬화하여 Page 형태로 반환
}