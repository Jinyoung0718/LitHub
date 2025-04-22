package com.sjy.LitHub.global.message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.config.SlackNotifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FanOutDLQListener {

	private final SlackNotifier slackNotifier;

	@RabbitListener(queues = RabbitMQConstants.FANOUT_DLQ)
	public void handleDeadLetter(FanOutMessage message) {
		log.warn("[DLQ] 소비 실패한 메시지 수신: {}", message);
		slackNotifier.sendFanOutFailure(message.getUserId(), message.getPostId());
	}
}