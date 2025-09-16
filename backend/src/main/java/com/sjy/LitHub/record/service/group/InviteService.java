package com.sjy.LitHub.record.service.group;

import static com.sjy.LitHub.global.model.BaseResponseStatus.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidGroupException;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.record.entity.StudyGroup;
import com.sjy.LitHub.record.model.group.GroupCreateRequestDTO;
import com.sjy.LitHub.record.model.group.RoomMetaEventMessage;
import com.sjy.LitHub.record.model.group.RoomMetaEventType;
import com.sjy.LitHub.record.repository.group.StudyGroupRepository;
import com.sjy.LitHub.record.service.group.wave.WaitingRoomEventPublisher;
import com.sjy.LitHub.record.service.timer.util.TimerRedisUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InviteService {

	private final GroupAccessValidator groupAccessValidator;
	private final StudyGroupRepository studyGroupRepository;
	private final UserRepository userRepository;
	private final WaitingRoomEventPublisher waitingRoomEventPublisher;
	private final NotificationService notificationService;
	private final TimerRedisUtils timerRedisUtils;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	@Transactional
	public Long createGroup(GroupCreateRequestDTO request) {
		User owner = AuthUser.getAuthUser();
		StudyGroup group = StudyGroup.of(request.getTitle(), request.getContent(), owner);
		studyGroupRepository.save(group);
		return group.getId();
	}

	@Transactional
	public void inviteUserToRoom(Long roomId, Long ownerId, Long targetUserId) {
		groupAccessValidator.validateOwnerBeforeStartOrThrow(roomId, ownerId);
		if (!userRepository.existsById(targetUserId)) {
			throw new InvalidUserException(USER_NOT_FOUND);
		}
		notificationService.createInvite(roomId, targetUserId);
	}

	public void joinRoom(Long roomId, Long userId) {
		notificationService.consumeInviteOrThrow(roomId, userId);

		timerRedisUtils.addParticipant(roomId, userId);
		waitingRoomEventPublisher.publish(
			RoomMetaEventMessage.of(roomId, userId, RoomMetaEventType.JOINED)
		);
	}

	@Transactional
	public void exitRoom(Long roomId, Long userId) {
		StudyGroup group = studyGroupRepository.findById(roomId)
			.orElseThrow(() -> new InvalidGroupException(GROUP_NOT_OWNER));

		boolean isOwner = group.getOwner().getId().equals(userId);
		boolean started = timerRedisUtils.isTimerStarted(roomId);

		if (isOwner && !started) {
			group.markAsCanceled();

			afterCommitExecutor.executeAfterCommit(() -> {
				timerRedisUtils.cleanupWaitingRoom(roomId);
				waitingRoomEventPublisher.publish(
					RoomMetaEventMessage.of(roomId, userId, RoomMetaEventType.CANCELED)
				);
			});
			return;
		}

		timerRedisUtils.removeParticipant(roomId, userId);
		waitingRoomEventPublisher.publish(
			RoomMetaEventMessage.of(roomId, userId, RoomMetaEventType.EXITED)
		);
	}
}