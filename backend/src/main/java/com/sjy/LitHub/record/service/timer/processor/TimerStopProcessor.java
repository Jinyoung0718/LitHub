package com.sjy.LitHub.record.service.timer.processor;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.service.UserInfo.MyPageService;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.record.model.timer.TimerEventMessage;
import com.sjy.LitHub.record.model.timer.TimerEventType;
import com.sjy.LitHub.record.repository.group.StudyGroupRepository;
import com.sjy.LitHub.record.service.sse.SSEEmitterService;
import com.sjy.LitHub.record.service.timer.TimerHeartbeatService;
import com.sjy.LitHub.record.service.timer.util.TimerRedisUtils;
import com.sjy.LitHub.record.service.timer.util.TimerValidator;
import com.sjy.LitHub.record.service.timer.wave.TimerEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimerStopProcessor {

	private final TimerRedisUtils timerRedisUtils;
	private final TimerValidator timerValidator;
	private final TimerHeartbeatService heartbeatService;
	private final MyPageService myPageService;
	private final SSEEmitterService sseEmitterService;
	private final TimerEventPublisher eventPublisher;
	private final StudyGroupRepository studyGroupRepository;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	@Transactional
	public void process(Long roomId, Long requesterId) {
		Long ownerId = validateAndGetOwner(roomId, requesterId);
		Set<Long> participantIds = timerRedisUtils.getParticipantIdsForRoom(roomId);
		int elapsedMinutes = calculateElapsedMinutes(roomId);
		saveResults(roomId, participantIds, elapsedMinutes);

		afterCommitExecutor.executeAfterCommit(() -> {
			cleanupRedis(roomId);
			notifyClients(roomId, ownerId);
		});
	}

	private Long validateAndGetOwner(Long roomId, Long requesterId) {
		Map<String, String> timerFields = timerRedisUtils.getValidatedTimerFieldsForRoom(roomId);
		Long ownerId = Long.parseLong(timerFields.get(TIMER_FIELD_OWNER_ID));
		if (requesterId != null) {
			timerValidator.validateOwner(ownerId, requesterId);
		}
		return ownerId;
	}

	private int calculateElapsedMinutes(Long roomId) {
		Map<String, String> fields = timerRedisUtils.getValidatedTimerFieldsForRoom(roomId);
		LocalDateTime startTime = LocalDateTime.parse(fields.get(TIMER_FIELD_START_TIME));
		int accumulated = Integer.parseInt(fields.getOrDefault(TIMER_FIELD_ACCUMULATED_MINUTES, "0"));

		LocalDateTime endTime = Boolean.parseBoolean(fields.get(TIMER_FIELD_PAUSED))
			? LocalDateTime.parse(fields.get(TIMER_FIELD_PAUSE_START_TIME))
			: LocalDateTime.now();

		return accumulated + (int) Duration.between(startTime, endTime).toMinutes();
	}

	private void saveResults(Long roomId, Set<Long> participantIds, int elapsedMinutes) {
		myPageService.saveGroupReadingSession(participantIds, elapsedMinutes);
		studyGroupRepository.findById(roomId)
			.ifPresent(group -> group.markAsEnded(elapsedMinutes, participantIds));
	}

	private void cleanupRedis(Long roomId) {
		timerRedisUtils.cleanupWaitingRoom(roomId);
		heartbeatService.cleanupDeadTimer(roomId);
	}

	private void notifyClients(Long roomId, Long ownerId) {
		eventPublisher.publish(TimerEventMessage.of(roomId, TimerEventType.TIMER_STOPPED, ownerId));
		sseEmitterService.disconnectRoom(roomId);
	}
}