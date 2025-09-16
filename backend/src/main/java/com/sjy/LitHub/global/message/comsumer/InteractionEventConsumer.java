package com.sjy.LitHub.global.message.comsumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.message.model.LikeToggledEvent;
import com.sjy.LitHub.global.message.model.ScrapToggledEvent;
import com.sjy.LitHub.global.message.utils.RabbitMQConstants;
import com.sjy.LitHub.post.cache.interaction.InteractionWriteService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InteractionEventConsumer {

	private final InteractionWriteService interactionWriteService;

	@RabbitListener(queues = RabbitMQConstants.LIKE_QUEUE)
	public void handleLikeEvent(LikeToggledEvent event) {
		if (event.liked()) {
			interactionWriteService.onLike(event.postId(), event.userId());
		} else {
			interactionWriteService.onUnlike(event.postId(), event.userId());
		}
	}

	@RabbitListener(queues = RabbitMQConstants.SCRAP_QUEUE)
	public void handleScrapEvent(ScrapToggledEvent event) {
		if (event.scrapped()) {
			interactionWriteService.onScrap(event.postId(), event.userId());
		} else {
			interactionWriteService.onUnscrap(event.postId(), event.userId());
		}
	}
}