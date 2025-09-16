package com.sjy.LitHub.global.message.comsumer;

import static com.sjy.LitHub.global.message.utils.RabbitMQConstants.*;

import java.util.List;
import java.util.Objects;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.repository.follow.FollowRepository;
import com.sjy.LitHub.global.message.model.PostDeletedEvent;
import com.sjy.LitHub.post.cache.enums.CachePolicy;

@Component
public class FeedCleanupConsumer {

	private final RedisTemplate<String, String> redisTemplate;
	private final FollowRepository followRepository;

	public FeedCleanupConsumer(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		FollowRepository followRepository
	) {
		this.redisTemplate = redisTemplate;
		this.followRepository = followRepository;
	}

	@RabbitListener(queues = POST_DELETED_QUEUE)
	public void handlePostDeleted(PostDeletedEvent event) {
		Long postId = event.getPostId();
		Long authorId = event.getAuthorId();

		List<Long> followerIds = followRepository.findFollowerIdsByUserId(authorId);
		if (followerIds.isEmpty()) {
			return;
		}

		StringRedisSerializer ser = new StringRedisSerializer();
		byte[] member = ser.serialize(postId.toString());

		redisTemplate.executePipelined((RedisCallback<Object>) conn -> {
			followerIds.stream()
				.map(fid -> ser.serialize(CachePolicy.FEED_POST.createKey(fid)))
				.filter(Objects::nonNull)
				.forEach(feedKey -> conn.zSetCommands().zRem(feedKey, member));
			return null;
		});
	}
}