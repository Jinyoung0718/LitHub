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
	} // 사용자가 피드를 요청했을 때 피드 목록을 Redis 에서 빠르게 불러오는 데 사용

	public void addPostToMultipleFeeds(List<Long> userIds, Long postId, int maxFeedSize) {
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {

			// 파이프라인을 이용해 명령을 한 번에 전송
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
	} // 새로운 게시글을 여러 사용자의 피드에 동시에 삽입
}