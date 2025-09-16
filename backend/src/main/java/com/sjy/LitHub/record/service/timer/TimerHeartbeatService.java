package com.sjy.LitHub.record.service.timer;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TimerHeartbeatService {

	private final RedisTemplate<String, String> redisTemplate;
	public static final Duration HEARTBEAT_TTL = Duration.ofSeconds(30);
	public static final String HEARTBEAT_VALUE = "ALIVE";

	public TimerHeartbeatService(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void receiveHeartbeat(Long roomId, Long userId) {
		String timerKey = String.format(TIMER_HASH_KEY_FORMAT, roomId);
		String ownerId = (String) redisTemplate.opsForHash().get(timerKey, TIMER_FIELD_OWNER_ID);
		if (ownerId == null || !ownerId.equals(userId.toString())) return;

		// 하트비트 TTL 갱신
		String heartbeatKey = String.format(HEARTBEAT_KEY_FORMAT, roomId);
		redisTemplate.opsForValue().set(heartbeatKey, HEARTBEAT_VALUE, HEARTBEAT_TTL);

		// ZSET 만료 인덱스 갱신 -> 만료된 방만 효율적으로 스케쥴링 하기 위함 (O(log n) + K) -> SSE 해제 /DB 업데이트
		long expireAtMillis = System.currentTimeMillis() + HEARTBEAT_TTL.toMillis();
		redisTemplate.opsForZSet().add(ROOMS_HEARTBEAT_ZSET, String.valueOf(roomId), expireAtMillis);
	}

	public boolean isHeartbeatAlive(Long roomId) {
		String heartbeatKey = String.format(HEARTBEAT_KEY_FORMAT, roomId);
		return Boolean.TRUE.equals(redisTemplate.hasKey(heartbeatKey));
	}

	public void cleanupDeadTimer(Long roomId) {
		String timerKey = String.format(TIMER_HASH_KEY_FORMAT, roomId);
		String heartbeatKey = String.format(HEARTBEAT_KEY_FORMAT, roomId);

		redisTemplate.delete(timerKey);
		redisTemplate.delete(heartbeatKey);
		redisTemplate.opsForZSet().remove(ROOMS_HEARTBEAT_ZSET, String.valueOf(roomId));
	}
}