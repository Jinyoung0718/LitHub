package com.sjy.LitHub.global.message;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.repository.follow.FollowRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FanOutConsumer {

	private final FollowRepository followRepository;
	private final FeedRedisUtil feedRedisUtil;

	@RabbitListener(queues = RabbitMQConstants.FANOUT_QUEUE)
	public void handleFanOut(FanOutMessage message) {
		Long authorId = message.getUserId();
		Long postId = message.getPostId();
		log.info("[FanOutConsumer] 메시지 수신: {}", message);

		List<Long> followerIds = followRepository.findFollowerIdsByUserId(authorId);
		if (followerIds.isEmpty()) {
			log.info("[FanOutConsumer] 팔로워 없음. fan-out 생략");
			return;
		}

		feedRedisUtil.addPostToMultipleFeeds(followerIds, postId, 200);
		log.info("[FanOutConsumer] {}명의 팔로워에게 fan-out 완료", followerIds.size());
	}
}