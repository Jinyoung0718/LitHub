package com.sjy.LitHub.post.cache.post;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.InteractionType;

@Component
public class PostInteractionRedisManager {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String ALL_POST_IDS_KEY = "post:interactions:ids";

	public PostInteractionRedisManager(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void toggleInteraction(Long postId, Long userId, InteractionType type) {
		String key = type.key(postId);
		String userIdStr = userId.toString();
		BoundSetOperations<String, String> ops = redisTemplate.boundSetOps(key);

		if (Boolean.TRUE.equals(ops.isMember(userIdStr))) {
			ops.remove(userIdStr);
		} else {
			ops.add(userIdStr);
			redisTemplate.opsForSet().add(ALL_POST_IDS_KEY, postId.toString());
		}
	}

	public boolean hasInteraction(Long postId, Long userId, InteractionType type) {
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(type.key(postId), userId.toString()));
	}

	public long getInteractionCount(Long postId, InteractionType type) {
		Long size = redisTemplate.opsForSet().size(type.key(postId));
		return size != null ? size : 0L;
	}

	public Set<Long> getAllPostIds() {
		Set<String> keys = redisTemplate.keys(CachePolicy.POST_INTERACTION.createKey("likes", "*"));
		if (keys == null) return Set.of();

		return keys.stream()
			.map(k -> k.replace("post:likes:", ""))
			.map(Long::parseLong)
			.collect(Collectors.toSet());
	}

	public Set<Long> getInteractions(Long postId, InteractionType type) {
		Set<String> members = redisTemplate.opsForSet().members(type.key(postId));
		if (members == null) return Set.of();

		return members.stream().map(Long::parseLong).collect(Collectors.toSet());
	}

	public void clearInteractions(Long postId) {
		redisTemplate.delete(InteractionType.LIKE.key(postId));
		redisTemplate.delete(InteractionType.SCRAP.key(postId));
	}
}