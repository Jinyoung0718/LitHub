package com.sjy.LitHub.post.cache.interaction;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.InteractionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InteractionCacheUtils {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final Duration TTL = Duration.ofMinutes(10);

	public InteractionCacheUtils(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper
	) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public void addToSet(String setKey, String value) {
		redisTemplate.opsForSet().add(setKey, value);
	}

	public void removeFromSet(String setKey, String value) {
		redisTemplate.opsForSet().remove(setKey, value);
	}

	public boolean isMember(String setKey, String value) {
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(setKey, value));
	}

	public void incrementIfExists(String key) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			redisTemplate.opsForValue().increment(key);
		}
	}

	public void decrementIfExists(String key) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			redisTemplate.opsForValue().decrement(key);
		}
	}

	public void updatePopularity(Long postId, InteractionType type) {
		String zsetKey = CachePolicy.POPULAR_POST_ZSET.getKeyFormat();
		redisTemplate.opsForZSet().incrementScore(zsetKey, postId.toString(), type.getWeight());
	}

	public long getCount(String key) {
		try {
			String value = redisTemplate.opsForValue().get(key);
			return value != null ? Long.parseLong(value) : 0L;
		} catch (Exception e) {
			log.warn("[Redis count 조회 실패] key: {}, error: {}", key, e.getMessage());
			return 0L;
		}
	}

	public String createInteractionKey(Long postId, Long userId) {
		return String.format("post:interaction:%d:%d", postId, userId);
	}

	public void putInteractionCache(Long postId, Long userId, PostInteractionState state) {
		String key = createInteractionKey(postId, userId);
		try {
			String json = objectMapper.writeValueAsString(state);
			redisTemplate.opsForValue().set(key, json, TTL);
		} catch (Exception e) {
			log.warn("[Interaction 캐시 저장 실패] key: {}, error: {}", key, e.getMessage());
		}
	}

	public void deleteInteractionCache(Long postId, Long userId) {
		String key = createInteractionKey(postId, userId);
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.warn("[Interaction 캐시 삭제 실패] key: {}, error: {}", key, e.getMessage());
		}
	}

	public PostInteractionState getInteractionState(String cacheKey) {
		try {
			String json = redisTemplate.opsForValue().get(cacheKey);
			if (json != null) {
				redisTemplate.expire(cacheKey, TTL);
				return objectMapper.readValue(json, PostInteractionState.class);
			}
		} catch (Exception e) {
			log.warn("[Interaction 캐시 조회 실패] key: {}, error: {}", cacheKey, e.getMessage());
		}
		return null;
	}

	public List<Long> getTopPostIds(int size) {
		String zsetKey = CachePolicy.POPULAR_POST_ZSET.getKeyFormat();
		Set<String> topIds = redisTemplate.opsForZSet().reverseRange(zsetKey, 0, size - 1);
		if (topIds == null || topIds.isEmpty()) {
			return List.of();
		}

		return topIds.stream().map(Long::parseLong).toList();
	}
}