package com.sjy.LitHub.record.service.timer.scheduler;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredRoomScheduler {

	private final ExpiredRoomService expiredRoomService;
	private final RoomCloseService roomCloseService;

	@Scheduled(fixedDelay = REAPER_FIXED_DELAY_MS) // 10 초 간격
	@SchedulerLock(
		name = REAPER_NAME,
		lockAtMostFor = REAPER_LOCK_AT_MOST,
		lockAtLeastFor = REAPER_LOCK_AT_LEAST
	)
	public void reap() {
		expiredRoomService.findExpiredRoomIds()
			.forEach(roomCloseService::closeRoomIfExpired);
	}

	// 여러 인스턴스가 동시에 같은 방을 만료시키려고 하면 중복 종료 처리
	// 분산락으로 하나의 인스턴스만 이 스케줄러를 실행
}