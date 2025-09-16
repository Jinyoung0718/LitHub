package com.sjy.LitHub.global.message.comsumer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.repository.follow.FollowRepository;
import com.sjy.LitHub.global.message.model.FanOutMessage;
import com.sjy.LitHub.global.message.utils.FeedRedisUtil;
import com.sjy.LitHub.global.message.utils.RabbitMQConstants;

@Component
public class FanOutConsumer {

	private final FollowRepository followRepository;
	private final FeedRedisUtil feedRedisUtil;
	private final RedisTemplate<String, String> redisTemplate;

	public FanOutConsumer(
		FollowRepository followRepository,
		FeedRedisUtil feedRedisUtil,
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.followRepository = followRepository;
		this.feedRedisUtil = feedRedisUtil;
		this.redisTemplate = redisTemplate;
	}

	@RabbitListener(queues = RabbitMQConstants.FANOUT_QUEUE)
	public void handleFanOut(FanOutMessage message) {
		String dedupKey = "fanout:processed:" + message.getPostId();

		// 이미 처리한 메시지면 스킵
		Boolean alreadyProcessed = redisTemplate.hasKey(dedupKey);
		if (Boolean.TRUE.equals(alreadyProcessed)) {
			return;
		}

		redisTemplate.opsForValue().set(
			dedupKey,
			"1",
			RabbitMQConstants.FANOUT_PROCESSED_KEY_TTL_SECONDS,
			TimeUnit.SECONDS
		);

		Long authorId = message.getUserId();
		Long postId = message.getPostId();
		long createdAtMillis = message.getCreatedAtMillis();

		List<Long> followerIds = followRepository.findFollowerIdsByUserId(authorId);
		if (followerIds.isEmpty()) return;

		feedRedisUtil.addPostToMultipleFeeds(
			followerIds,
			postId,
			createdAtMillis,
			RabbitMQConstants.FANOUT_MAX_FEED_SIZE,
			RabbitMQConstants.FANOUT_FEED_TTL_DAYS
		);
	}
}