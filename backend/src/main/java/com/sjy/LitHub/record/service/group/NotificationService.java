package com.sjy.LitHub.record.service.group;

import static com.sjy.LitHub.global.model.BaseResponseStatus.*;
import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.exception.custom.InvalidGroupException;
import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.record.entity.StudyGroup;
import com.sjy.LitHub.record.model.NotificationResponseDTO;
import com.sjy.LitHub.record.repository.group.StudyGroupRepository;

@Service
public class NotificationService {

	private final RedisTemplate<String, String> redisTemplate;
	private final StudyGroupRepository studyGroupRepository;

	public NotificationService(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		StudyGroupRepository studyGroupRepository
	) {
		this.redisTemplate = redisTemplate;
		this.studyGroupRepository = studyGroupRepository;
	}

	public void createInvite(Long roomId, Long targetUserId) {
		String inviteKey = NotificationUtil.inviteKey(targetUserId, roomId);
		String indexKey = NotificationUtil.inviteIndexKey(targetUserId);

		redisTemplate.opsForValue().set(inviteKey, "", INVITE_TTL);
		redisTemplate.opsForSet().add(indexKey, String.valueOf(roomId));
	}

	@Transactional(readOnly = true)
	public List<NotificationResponseDTO> getInviteNotifications(Long userId) {
		String indexKey = NotificationUtil.inviteIndexKey(userId);
		Set<String> roomIds = redisTemplate.opsForSet().members(indexKey);
		if (roomIds == null || roomIds.isEmpty()) return List.of();

		return roomIds.stream()
			.map(Long::parseLong)

			.filter(roomId -> redisTemplate.hasKey(NotificationUtil.inviteKey(userId, roomId)))

			.map(roomId -> studyGroupRepository.findInviteNotificationByGroupId(roomId)
				.orElseThrow(() -> new InvalidGroupException(GROUP_NOT_OWNER)))

			.toList();
	}

	public void clearInvite(Long roomId, Long userId) {
		String inviteKey = NotificationUtil.inviteKey(userId, roomId);
		String indexKey = NotificationUtil.inviteIndexKey(userId);

		redisTemplate.delete(inviteKey);
		redisTemplate.opsForSet().remove(indexKey, String.valueOf(roomId));
	}

	public void consumeInviteOrThrow(Long roomId, Long userId) {
		String inviteKey = NotificationUtil.inviteKey(userId, roomId);
		Boolean exists = redisTemplate.hasKey(inviteKey);
		if (exists == null || !exists) {
			throw new InvalidRedisException(BaseResponseStatus.GROUP_NOT_FOUND);
		}

		StudyGroup group = studyGroupRepository.findById(roomId)
			.orElseThrow(() -> new InvalidGroupException(BaseResponseStatus.GROUP_NOT_FOUND));

		switch (group.getStatus()) {
			case CANCELED -> throw new InvalidGroupException(BaseResponseStatus.GROUP_CANCELED);
			case RUNNING, ENDED -> throw new InvalidGroupException(BaseResponseStatus.GROUP_ALREADY_STARTED);
			default -> {}
		}

		clearInvite(roomId, userId);
	}
}