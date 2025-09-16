package com.sjy.LitHub.record.service.timer.processor;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.record.entity.StudyGroup;
import com.sjy.LitHub.record.model.timer.TimerEventMessage;
import com.sjy.LitHub.record.model.timer.TimerEventType;
import com.sjy.LitHub.record.repository.group.StudyGroupRepository;
import com.sjy.LitHub.record.service.timer.TimerHeartbeatService;
import com.sjy.LitHub.record.service.timer.util.TimerRedisUtils;
import com.sjy.LitHub.record.service.timer.wave.TimerEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimerStartProcessor {

	private static final String INITIAL_PAUSED = "false";
	private static final String INITIAL_ACCUMULATED_MINUTES = "0";

	private final TimerRedisUtils timerRedisUtils;
	private final TimerEventPublisher eventPublisher;
	private final TimerHeartbeatService heartbeatService;
	private final StudyGroupRepository studyGroupRepository;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	@Transactional
	public void process(Long roomId, Long ownerId) {
		String startTime = LocalDateTime.now().toString();
		String timerKey = String.format(TIMER_HASH_KEY_FORMAT, roomId);

		Map<String, String> timerFields = Map.of(
			TIMER_FIELD_START_TIME, startTime,
			TIMER_FIELD_OWNER_ID, String.valueOf(ownerId),
			TIMER_FIELD_PAUSED, INITIAL_PAUSED,
			TIMER_FIELD_ACCUMULATED_MINUTES, INITIAL_ACCUMULATED_MINUTES
		);

		timerRedisUtils.putAllHash(timerKey, timerFields);
		studyGroupRepository.findById(roomId).ifPresent(StudyGroup::markAsStarted);

		afterCommitExecutor.executeAfterCommit(() -> {
			eventPublisher.publish(TimerEventMessage.of(roomId, TimerEventType.TIMER_STARTED, ownerId));
			heartbeatService.receiveHeartbeat(roomId, ownerId);
		});
	}
}