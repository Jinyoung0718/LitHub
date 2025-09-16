package com.sjy.LitHub.record.service.timer.processor;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.record.model.timer.TimerEventMessage;
import com.sjy.LitHub.record.model.timer.TimerEventType;
import com.sjy.LitHub.record.service.timer.util.TimerRedisUtils;
import com.sjy.LitHub.record.service.timer.util.TimerValidator;
import com.sjy.LitHub.record.service.timer.wave.TimerEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimerPauseProcessor {

	private final TimerRedisUtils timerRedisUtils;
	private final TimerEventPublisher eventPublisher;
	private final TimerValidator validator;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	public void pause(Long roomId, Long requesterId) {
		update(roomId, requesterId, true);
	}

	public void resume(Long roomId, Long requesterId) {
		update(roomId, requesterId, false);
	}

	private void update(Long roomId, Long requesterId, boolean pause) {
		validator.validateHeartbeatOrThrow(roomId);
		Map<String, String> timerFields = timerRedisUtils.getValidatedTimerFieldsForRoom(roomId);

		Long ownerId = Long.parseLong(timerFields.get(TIMER_FIELD_OWNER_ID));
		validator.validateOwner(ownerId, requesterId);
		validator.validatePauseState(timerFields.get(TIMER_FIELD_PAUSED), pause);

		if (pause) {
			timerRedisUtils.pauseTimerForRoom(roomId);
		} else {
			timerRedisUtils.resumeTimerForRoom(roomId, timerFields);
		}

		afterCommitExecutor.executeAfterCommit(() -> {
			TimerEventType type = pause ? TimerEventType.TIMER_PAUSED : TimerEventType.TIMER_RESUMED;
			eventPublisher.publish(TimerEventMessage.of(roomId, type, ownerId));
		});
	}
}