package com.sjy.LitHub.record.service.timer.wave;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.record.model.timer.TimerEventMessage;
import com.sjy.LitHub.record.model.timer.TimerEventType;
import com.sjy.LitHub.record.service.sse.SSEEmitterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimerEventSubscriber implements MessageListener {

	private final ObjectMapper objectMapper;
	private final SSEEmitterService sseEmitterService;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String body = new String(message.getBody(), StandardCharsets.UTF_8);
			TimerEventMessage event = objectMapper.readValue(body, TimerEventMessage.class);

			Long roomId = event.getRoomId();
			if (roomId == null) return;

			sseEmitterService.sendToRoom(roomId, event);
			if (event.getType() == TimerEventType.TIMER_STOPPED) {
				sseEmitterService.disconnectRoom(roomId);
			}

		} catch (JsonProcessingException e) {
			log.error("타이머 이벤트 역직렬화 실패", e);
			throw new InvalidRedisException(BaseResponseStatus.REDIS_DESERIALIZATION_FAILED);
		} catch (Exception e) {
			log.error("타이머 이벤트 처리 실패", e);
			throw new InvalidRedisException(BaseResponseStatus.REDIS_CACHE_UPDATE_FAILED);
		}
	}
}