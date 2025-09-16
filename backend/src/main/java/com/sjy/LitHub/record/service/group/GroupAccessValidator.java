package com.sjy.LitHub.record.service.group;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sjy.LitHub.global.exception.custom.InvalidGroupException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.record.repository.group.StudyGroupRepository;

@Service
public class GroupAccessValidator {

	private final RedisTemplate<String, String> redisTemplate;
	private final StudyGroupRepository studyGroupRepository;

	public GroupAccessValidator(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		StudyGroupRepository studyGroupRepository
	) {
		this.redisTemplate = redisTemplate;
		this.studyGroupRepository = studyGroupRepository;
	}

	public void validateOwnerBeforeStartOrThrow(Long roomId, Long ownerId) {
		boolean isValid = studyGroupRepository.existsByIdAndOwnerId(roomId, ownerId);
		if (!isValid) {
			throw new InvalidGroupException(BaseResponseStatus.GROUP_NOT_OWNER);
		}

		String timerKey = String.format(TIMER_HASH_KEY_FORMAT, roomId);
		String started = redisTemplate.<String, String>opsForHash().get(timerKey, TIMER_FIELD_START_TIME);

		if (started != null) {
			throw new InvalidGroupException(BaseResponseStatus.GROUP_ALREADY_STARTED);
		}
	}
}