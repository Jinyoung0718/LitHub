package com.sjy.LitHub.global.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FanOutProducer {

	private final RabbitTemplate rabbitTemplate;

	public void sendMessage(FanOutMessage message) {
		log.info("[FanOut] 게시글 생성 이벤트 발행: {}", message);
		rabbitTemplate.convertAndSend(RabbitMQConstants.FANOUT_EXCHANGE, "", message);
	}
}