package com.sjy.LitHub.post.cache.interaction;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.InteractionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InteractionCacheStore {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public InteractionCacheStore(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		ObjectMapper objectMapper
	) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	// Set 조작
	public void addToSet(String key, String value) {
		redisTemplate.opsForSet().add(key, value);
	}

	public void removeFromSet(String key, String value) {
		redisTemplate.opsForSet().remove(key, value);
	}

	public boolean isMember(String key, String value) {
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
	}

	// Count 조작
	public void increment(String key) {
		redisTemplate.opsForValue().increment(key);
	}

	public void safeDecrement(String key) {
		Long v = redisTemplate.opsForValue().decrement(key);
		if (v == null || v < 0) {
			redisTemplate.opsForValue().set(key, "0");
		}
	}

	public long getCount(String key) {
		String value = redisTemplate.opsForValue().get(key);
		return value != null ? Long.parseLong(value) : 0L;
	}

	public Map<Long, Long> getCountsBulk(Collection<Long> postIds, CachePolicy policy) {
		if (postIds == null || postIds.isEmpty()) return Map.of();

		List<String> keys = postIds.stream()
			.map(policy::createKey)
			.toList();
		List<String> values = redisTemplate.opsForValue().multiGet(keys);

		Map<Long, Long> countsByPostId = new LinkedHashMap<>(postIds.size());
		Iterator<String> iterator = (values != null) ? values.iterator() : Collections.emptyIterator();

		for (Long postId : postIds) {
			String raw = iterator.hasNext() ? iterator.next() : null;
			if (raw != null) {
				countsByPostId.put(postId, Long.parseLong(raw));
			} else {
				countsByPostId.put(postId, null);
			}
		}

		return countsByPostId;
	}

	public void putCountsBulk(Map<Long, Long> counts, CachePolicy policy) {
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			StringRedisSerializer ser = new StringRedisSerializer();
			counts.forEach((postId, value) -> {
				byte[] k = ser.serialize(policy.createKey(postId));
				byte[] v = ser.serialize(String.valueOf(value));
				connection.stringCommands().set(Objects.requireNonNull(k), Objects.requireNonNull(v));
			});
			return null;
		});
	}

	// 인기글
	public void updatePopularity(Long postId, InteractionType type) {
		String key = CachePolicy.POPULAR_POST_ZSET.getKeyFormat();
		redisTemplate.opsForZSet().incrementScore(key, postId.toString(), type.getWeight());
	}

	public List<Long> getTopPostIdsRange(long start, long end) {
		String key = CachePolicy.POPULAR_POST_ZSET.getKeyFormat();
		Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, start, end);

		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		return ids.stream()
			.map(Long::parseLong)
			.toList();
	}

	// 사용자별 인터랙션 JSON 캐시
	public void putInteractionCache(Long postId, Long userId, PostInteractionState state) {
		String key = CachePolicy.POST_INTERACTION.createKey(postId, userId);
		try {
			String json = objectMapper.writeValueAsString(state);
			Duration ttl = CachePolicy.POST_INTERACTION.getTtl();
			redisTemplate.opsForValue().set(key, json, ttl);
		} catch (JsonProcessingException e) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_CACHE_UPDATE_FAILED);
		}
	}

	public PostInteractionState getInteractionState(Long postId, Long userId) {
		String key = CachePolicy.POST_INTERACTION.createKey(postId, userId);
		String json = redisTemplate.opsForValue().get(key);
		if (json != null) {
			try {
				Duration ttl = CachePolicy.POST_INTERACTION.getTtl();
				redisTemplate.expire(key, ttl);
				return objectMapper.readValue(json, PostInteractionState.class);
			} catch (JsonProcessingException e) {
				throw new InvalidRedisException(BaseResponseStatus.REDIS_DESERIALIZATION_FAILED);
			}
		}
		return null;
	}

	public void deleteInteractionCache(Long postId, Long userId) {
		redisTemplate.delete(CachePolicy.POST_INTERACTION.createKey(postId, userId));
	}
}