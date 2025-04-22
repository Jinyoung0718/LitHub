package com.sjy.LitHub.global.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;

@Component
public class FeedRedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	public FeedRedisUtil(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public List<Long> getFeedPostIds(Long userId, int size) {
		String key = CachePolicy.FEED_POST.createKey(userId);
		List<String> rawPostIds = redisTemplate.opsForList().range(key, 0, size - 1);
		if (rawPostIds == null || rawPostIds.isEmpty()) return List.of();

		return rawPostIds.stream()
			.map(Long::valueOf)
			.toList();
	}

	public void addPostToMultipleFeeds(List<Long> userIds, Long postId, int maxFeedSize) {
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			StringRedisSerializer serializer = new StringRedisSerializer();

			for (Long userId : userIds) {
				String redisKey = CachePolicy.FEED_POST.createKey(userId);
				String postValue = postId.toString();

				byte[] keyBytes = serializer.serialize(redisKey);
				byte[] valueBytes = serializer.serialize(postValue);

				if (keyBytes != null && valueBytes != null) {
					connection.listCommands().lPush(keyBytes, valueBytes);
					connection.listCommands().lTrim(keyBytes, 0, maxFeedSize - 1);
				}
			}
			return null;
		});

	}
}