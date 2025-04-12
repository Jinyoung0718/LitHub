package com.sjy.LitHub.post.cache;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
public class PerCacheManager {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public PerCacheManager(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	private static final Duration TTL = Duration.ofMinutes(30);

	public <T> T fetch(String key, Supplier<T> dbFetcher, Class<T> type) {
		try {
			String cached = redisTemplate.opsForValue().get(key);
			if (cached == null) return recalculate(key, dbFetcher);

			PerWrapper wrapper = objectMapper.readValue(cached, PerWrapper.class);
			long now = System.currentTimeMillis();

			if (now >= wrapper.expireTime - wrapper.delta * Math.log(Math.random())) {
				return recalculate(key, dbFetcher);
			}

			// 현재 시간과 비교해서 PER 조건(now >= expireTime - delta * log(random()))이 충족되면 DB 다시 조회
			return objectMapper.convertValue(wrapper.data, type);
		} catch (Exception e) {
			return recalculate(key, dbFetcher);
		}
	}

	private <T> T recalculate(String key, Supplier<T> fetcher) {
		long start = System.currentTimeMillis();
		T data = fetcher.get();
		long delta = System.currentTimeMillis() - start;

		try {
			PerWrapper wrapper = new PerWrapper(data, delta, System.currentTimeMillis() + TTL.toMillis());
			redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(wrapper), TTL);
		} catch (Exception ignored) {}

		return data;
	} // DB 조회한 데이터를 PerWrapper 로 감싸서 Redis 저장

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PerWrapper {
		private Object data;
		private long delta;
		private long expireTime;
	}
}