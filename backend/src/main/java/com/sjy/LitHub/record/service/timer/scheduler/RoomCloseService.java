package com.sjy.LitHub.record.service.timer.scheduler;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sjy.LitHub.record.service.timer.TimerHeartbeatService;
import com.sjy.LitHub.record.service.timer.processor.TimerStopProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoomCloseService {

	private final RedisTemplate<String, String> redisTemplate;
	private final TimerStopProcessor timerStopProcessor;
	private final TimerHeartbeatService timerHeartbeatService;

	public RoomCloseService(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		TimerStopProcessor timerStopProcessor,
		TimerHeartbeatService timerHeartbeatService) {
		this.redisTemplate = redisTemplate;
		this.timerStopProcessor = timerStopProcessor;
		this.timerHeartbeatService = timerHeartbeatService;
	}

	public void closeRoomIfExpired(Long roomId) {
		try {
			if (timerHeartbeatService.isHeartbeatAlive(roomId) || !acquireCloseLock(roomId)) {
				return;
			}
			timerStopProcessor.process(roomId, null);
		} catch (Exception e) {
			log.warn("방 종료 처리 중 오류 발생 roomId={}", roomId, e);
		} finally {
			cleanupFromIndex(roomId);
		}
	}

	private boolean acquireCloseLock(Long roomId) {
		String closedKey = String.format(ROOM_CLOSED_MARKER_FORMAT, roomId);
		return Boolean.TRUE.equals(
			redisTemplate.opsForValue().setIfAbsent(closedKey, "1", Duration.ofMinutes(5))
		);
	}

	private void cleanupFromIndex(Long roomId) {
		redisTemplate.opsForZSet().remove(ROOMS_HEARTBEAT_ZSET, String.valueOf(roomId));
	}
}
