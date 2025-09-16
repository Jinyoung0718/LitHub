package com.sjy.LitHub.record.service.timer.scheduler;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExpiredRoomService {

	private final RedisTemplate<String, String> redisTemplate;

	public ExpiredRoomService(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Set<Long> findExpiredRoomIds() {
		long now = System.currentTimeMillis();
		Set<String> candidates = redisTemplate.opsForZSet()
			.rangeByScore(ROOMS_HEARTBEAT_ZSET, 0, now, 0, REAPER_BATCH_SIZE);

		if (candidates == null || candidates.isEmpty()) return Set.of();

		return candidates.stream()
			.map(Long::parseLong)
			.collect(Collectors.toSet());
	}
}