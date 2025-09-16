package com.sjy.LitHub.post.cache.interaction;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.message.model.LikeToggledEvent;
import com.sjy.LitHub.global.message.model.ScrapToggledEvent;
import com.sjy.LitHub.global.message.utils.RabbitMQConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InteractionEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publishLikeToggled(LikeToggledEvent event) {
		rabbitTemplate.convertAndSend(RabbitMQConstants.LIKE_EXCHANGE, event);
	}

	public void publishScrapToggled(ScrapToggledEvent event) {
		rabbitTemplate.convertAndSend(RabbitMQConstants.SCRAP_EXCHANGE, event);
	}
}