package com.sjy.LitHub.record.service.timer.util;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.record.service.timer.TimerHeartbeatService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimerValidator {

	private final TimerHeartbeatService timerHeartbeatService;

	public void validateHeartbeatOrThrow(Long roomId) {
		if (!timerHeartbeatService.isHeartbeatAlive(roomId)) {
			timerHeartbeatService.cleanupDeadTimer(roomId);
			throw new InvalidRedisException(BaseResponseStatus.REDIS_TIMER_NOT_FOUND);
		}
	} // 타이머가 유효하게 살아 있는지 검증하고, 죽어있다면 정리 후 예외를 발생시킴

	public void validateFieldsExist(Map<String, String> fields) {
		if (fields.isEmpty()
			|| !fields.containsKey(TIMER_FIELD_OWNER_ID)
			|| !fields.containsKey(TIMER_FIELD_PAUSED)) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_TIMER_NOT_FOUND);
		}
	} // 필수 타이머 필드가 존재하는지 검증

	public void validateOwner(Long ownerId, Long requesterId) {
		if (!ownerId.equals(requesterId)) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_TIMER_NOT_OWNER);
		}
	} // 타이머의 소유자 여부를 검증

	public void validatePauseState(String paused, boolean pause) {
		if (pause && "true".equals(paused)) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_TIMER_ALREADY_PAUSED);
		}
		if (!pause && !"true".equals(paused)) {
			throw new InvalidRedisException(BaseResponseStatus.REDIS_TIMER_NOT_PAUSED);
		}
	} // 일시정지/재시작 상태 검증
}