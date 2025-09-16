package com.sjy.LitHub.record.service.timer.util;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TimerRedisUtils {

	private final RedisTemplate<String, String> redisTemplate;
	private final TimerValidator timerValidator;

	public TimerRedisUtils(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		TimerValidator timerValidator
	) {
		this.redisTemplate = redisTemplate;
		this.timerValidator = timerValidator;
	}

	private String timerKey(long roomId) {
		return String.format(TIMER_HASH_KEY_FORMAT, roomId);
	}

	private String heartbeatKey(long roomId)  {
		return String.format(HEARTBEAT_KEY_FORMAT, roomId);
	}

	private String participantKey(long roomId) {
		return String.format(TIMER_PARTICIPANT_SET_KEY_FORMAT, roomId);
	}

	public void putAllHash(String key, Map<String, String> fields) {
		redisTemplate.opsForHash().putAll(key, fields);
	}

	public Map<String, String> getValidatedTimerFieldsForRoom(Long roomId) {
		Map<Object, Object> raw = redisTemplate.opsForHash().entries(timerKey(roomId));
		Map<String, String> result = raw.entrySet().stream()
			.collect(Collectors.toMap(
				e -> String.valueOf(e.getKey()),
				e -> String.valueOf(e.getValue())
			));
		timerValidator.validateFieldsExist(result);
		return result;
	}

	public Set<Long> getParticipantIdsForRoom(Long roomId) {
		Set<String> stringIds = redisTemplate.opsForSet().members(participantKey(roomId));
		if (stringIds == null) return Set.of();
		return stringIds.stream().map(Long::parseLong).collect(Collectors.toSet());
	}

	public boolean isTimerStarted(Long roomId) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(timerKey(roomId)));
	}

	public void putHashFieldForRoom(Long roomId, String field, String value) {
		redisTemplate.opsForHash().put(timerKey(roomId), field, value);
	}

	public void deleteHashFieldForRoom(Long roomId, String field) {
		redisTemplate.opsForHash().delete(timerKey(roomId), field);
	}

	public void addParticipant(Long roomId, Long userId) {
		redisTemplate.opsForSet().add(participantKey(roomId), String.valueOf(userId));
	}

	public void removeParticipant(Long roomId, Long userId) {
		redisTemplate.opsForSet().remove(participantKey(roomId), String.valueOf(userId));
	}

	public void pauseTimerForRoom(Long roomId) {
		putHashFieldForRoom(roomId, TIMER_FIELD_PAUSE_START_TIME, LocalDateTime.now().toString());
		putHashFieldForRoom(roomId, TIMER_FIELD_PAUSED, "true");
	}

	public void resumeTimerForRoom(Long roomId, Map<String, String> timerFields) {
		String pauseStartStr = timerFields.get(TIMER_FIELD_PAUSE_START_TIME);
		if (pauseStartStr != null) {
			LocalDateTime pauseStart = LocalDateTime.parse(pauseStartStr);
			long pausedDuration = Duration.between(pauseStart, LocalDateTime.now()).toMinutes();

			int accumulated = Integer.parseInt(timerFields.getOrDefault(TIMER_FIELD_ACCUMULATED_MINUTES, "0"));
			int updated = accumulated + (int) pausedDuration;

			putHashFieldForRoom(roomId, TIMER_FIELD_ACCUMULATED_MINUTES, String.valueOf(updated));
			deleteHashFieldForRoom(roomId, TIMER_FIELD_PAUSE_START_TIME);
		}
		putHashFieldForRoom(roomId, TIMER_FIELD_START_TIME, LocalDateTime.now().toString());
		putHashFieldForRoom(roomId, TIMER_FIELD_PAUSED, "false");
	}

	public void cleanupWaitingRoom(Long roomId) {
		redisTemplate.delete(participantKey(roomId));
		redisTemplate.delete(timerKey(roomId));
		redisTemplate.delete(heartbeatKey(roomId));
		redisTemplate.opsForZSet().remove(ROOMS_HEARTBEAT_ZSET, String.valueOf(roomId));
	}
}