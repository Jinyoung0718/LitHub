package com.sjy.LitHub.post.cache;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.cache.enums.CachePolicy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PerCacheManager {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PerCacheManager(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public <T> T fetch(String key, Supplier<T> dbFetcher, Class<T> type) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached == null) return recalculate(key, dbFetcher);

			PerWrapper wrapper = objectMapper.readValue(cached, PerWrapper.class);
			long now = System.currentTimeMillis();

			if (now >= wrapper.expireTime - wrapper.delta * Math.log(Math.random())) {
				return recalculate(key, dbFetcher);
			} // PER 알고리즘 조건 만족 시 DB 재조회

			return objectMapper.convertValue(wrapper.data, type);
		} catch (Exception e) {
			log.warn("PER 캐시 fetch 실패 - key: {}, error: {}", key, e.getMessage());
			return recalculate(key, dbFetcher);
		}
	} // PER 캐싱 로직

	private <T> T recalculate(String key, Supplier<T> fetcher) {
		long start = System.currentTimeMillis();
		T data = fetcher.get();
		long delta = System.currentTimeMillis() - start;

		try {
			PerWrapper wrapper = new PerWrapper(data, delta, System.currentTimeMillis() + CachePolicy.POST_DETAIL.getTtl().toMillis());
			redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(wrapper), CachePolicy.POST_DETAIL.getTtl());
		} catch (Exception e) {
			log.warn("PER 캐시 저장 실패 - key: {}, error: {}", key, e.getMessage());
		}

		return data;
	} // DB 에서 재조회하고 TTL 과 함께 저장

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PerWrapper {
		private Object data;
		private long delta;       // DB 응답 시간
		private long expireTime;  // TTL 기준 만료 시간
	}
}