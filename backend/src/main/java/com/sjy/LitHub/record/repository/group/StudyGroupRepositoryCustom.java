package com.sjy.LitHub.record.repository.group;

import java.util.List;
import java.util.Optional;

import com.sjy.LitHub.account.model.res.StudyGroupHistoryDTO;
import com.sjy.LitHub.record.model.NotificationResponseDTO;

public interface StudyGroupRepositoryCustom {
	List<StudyGroupHistoryDTO> findRecentEndedWithMembersByUser(Long userId, int limit);

	Optional<NotificationResponseDTO> findInviteNotificationByGroupId(Long roomId);
}