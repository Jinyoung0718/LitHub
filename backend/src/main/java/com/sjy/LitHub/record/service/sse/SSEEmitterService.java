package com.sjy.LitHub.record.service.sse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.record.model.group.RoomMetaEventMessage;
import com.sjy.LitHub.record.model.timer.TimerEventMessage;
import com.sjy.LitHub.record.service.timer.util.TimerConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SSEEmitterService {

	private final Map<Long, Map<Long, SseEmitter>> roomEmitters = new ConcurrentHashMap<>();
	private final RedisTemplate<String, Object> streamRedisTemplate;
	private final ObjectMapper objectMapper;

	public SSEEmitterService(
		@Qualifier("StreamRedisTemplate") RedisTemplate<String, Object> streamRedisTemplate,
		ObjectMapper objectMapper
	) {
		this.streamRedisTemplate = streamRedisTemplate;
		this.objectMapper = objectMapper;
	}

	public SseEmitter connectRoom(Long roomId, Long userId, String lastEventId, String streamKey) {
		SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
		roomEmitters.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(userId, emitter);

		Runnable remove = () -> {
			roomEmitters.getOrDefault(roomId, Map.of()).remove(userId);
			if (roomEmitters.get(roomId).isEmpty()) {
				roomEmitters.remove(roomId);
			}
		};

		emitter.onCompletion(remove);
		emitter.onTimeout(remove);
		emitter.onError(e -> remove.run());

		// 재연결 시 missed events 처리
		if (lastEventId != null && !lastEventId.isBlank()) {
			replayMissedEvents(roomId, userId, lastEventId, streamKey);
		}

		return emitter;
	}

	public void disconnectRoom(Long roomId) {
		Map<Long, SseEmitter> emitters = roomEmitters.remove(roomId);
		if (emitters != null) {
			emitters.values().forEach(SseEmitter::complete);
		}
	}

	public void sendToRoom(Long roomId, TimerEventMessage message) {
		send(roomId, TimerConstants.EVENT_NAME_TIMER_STATUS_CHANGED, message);
	}

	public void sendToRoom(Long roomId, RoomMetaEventMessage message) {
		send(roomId, TimerConstants.EVENT_NAME_ROOM_META_CHANGED, message);
	}

	private <T> void send(Long roomId, String eventName, T message) {
		Map<Long, SseEmitter> emitters = roomEmitters.getOrDefault(roomId, Map.of());
		for (SseEmitter emitter : emitters.values()) {
			try {
				emitter.send(SseEmitter.event()
					.name(eventName)
					.data(message));
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void replayMissedEvents(Long roomId, Long userId, String lastEventId, String streamKey) {
		List<MapRecord<String, String, Object>> records =
			streamRedisTemplate.<String, Object>opsForStream().read(
				StreamReadOptions.empty(),
				StreamOffset.create(streamKey, ReadOffset.from(lastEventId))
			);

		SseEmitter emitter = roomEmitters.getOrDefault(roomId, Map.of()).get(userId);
		if (emitter == null || records == null || records.isEmpty()) {
			return;
		}

		for (MapRecord<String, String, Object> record : records) {
			try {
				String json = record.getValue().values().iterator().next().toString();
				Object event = objectMapper.readValue(json, Object.class);

				emitter.send(SseEmitter.event()
					.id(record.getId().getValue())
					.data(event));
			} catch (Exception e) {
				log.error("SSE 재전송 실패: {}", record.getId(), e);
				emitter.completeWithError(e);
			}
		}
	}
}