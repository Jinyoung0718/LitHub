package com.sjy.LitHub.record.service.group.wave;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.redis.RedisConstants;
import com.sjy.LitHub.record.model.group.RoomMetaEventMessage;
import com.sjy.LitHub.record.service.timer.util.TimerConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WaitingRoomEventPublisher {

	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, String> redisTemplate;
	private final RedisTemplate<String, Object> streamRedisTemplate;

	public WaitingRoomEventPublisher(
		ObjectMapper objectMapper,
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		@Qualifier("StreamRedisTemplate") RedisTemplate<String, Object> streamRedisTemplate
	) {
		this.objectMapper = objectMapper;
		this.redisTemplate = redisTemplate;
		this.streamRedisTemplate = streamRedisTemplate;
	}

	public void publish(RoomMetaEventMessage message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			redisTemplate.convertAndSend(TimerConstants.WAITING_ROOM_EVENT_TOPIC, json);

			streamRedisTemplate.opsForStream().add(
				StreamRecords.newRecord()
					.in(RedisConstants.WAITING_ROOM_EVENT_STREAM)
					.ofObject(json)
			);

			log.debug("대기방 이벤트 발행 및 스트림 기록: {}", json);
		} catch (JsonProcessingException e) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_DESERIALIZATION_FAILED);
		} catch (Exception e) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_CACHE_UPDATE_FAILED);
		}
	}
}