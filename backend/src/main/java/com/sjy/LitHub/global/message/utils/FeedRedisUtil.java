package com.sjy.LitHub.global.message.utils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

	public void addPostToMultipleFeeds(
		List<Long> userIds,
		Long postId,
		long createdAtMillis,
		int maxFeedSize,
		int feedTtlDays
	) {
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			StringRedisSerializer serializer = new StringRedisSerializer();
			byte[] valueBytes = serializer.serialize(postId.toString());

			for (Long userId : userIds) {
				String redisKey = CachePolicy.FEED_POST.createKey(userId);
				byte[] keyBytes = serializer.serialize(redisKey);
				if (keyBytes != null && valueBytes != null) {

					// 삽입
					connection.zSetCommands().zAdd(keyBytes, createdAtMillis, valueBytes);

					// 최신 N개 유지
					connection.zSetCommands().zRemRange(keyBytes, 0, -maxFeedSize - 1);

					// 30일 이상 지난 게시글 제거
					long cutoff = createdAtMillis - TimeUnit.DAYS.toMillis(feedTtlDays);
					connection.zSetCommands().zRemRangeByScore(
						keyBytes,
						Double.NEGATIVE_INFINITY,
						cutoff
					);

					connection.keyCommands().expire(
						keyBytes,
						feedTtlDays * 24L * 3600L
					);
				}
			}
			return null;
		});
	}

	public List<Long> getFeedPostIdsRange(Long userId, long start, long end) {
		String key = CachePolicy.FEED_POST.createKey(userId);
		Set<String> raw = redisTemplate.opsForZSet().reverseRange(key, start, end);
		if (raw == null || raw.isEmpty()) {
			return List.of();
		}
		return raw.stream().map(Long::valueOf).toList();
	}
}