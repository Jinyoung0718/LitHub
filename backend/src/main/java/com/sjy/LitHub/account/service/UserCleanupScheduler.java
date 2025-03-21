package com.sjy.LitHub.account.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.repository.friend.FriendRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.record.repository.ReadLog.ReadLogRepository;
import com.sjy.LitHub.record.repository.ReadLogStatus.ReadLogStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {

	private final UserRepository userRepository;
	private final ReadLogRepository readLogRepository;
	private final ReadLogStatsRepository readLogStatsRepository;
	private final FriendRepository friendRepository;

	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void deleteUsersMarkedAsDeleted() {
		LocalDateTime threshold = LocalDateTime.now().minusDays(30);
		List<Long> userIdsToDelete = userRepository.findDeletedUserIdsBefore(threshold);

		if (userIdsToDelete.isEmpty()) {
			log.info("[회원 삭제 스케줄러] 삭제 대상 없음");
			return;
		}

		log.info("[회원 삭제 스케줄러] 삭제 대상 인원: {}", userIdsToDelete.size());

		for (Long userId : userIdsToDelete) {
			readLogRepository.deleteByUserId(userId);
			readLogStatsRepository.deleteByUserId(userId);
			friendRepository.deleteAllByUserId(userId);
			userRepository.deletePhysicallyById(userId);

			log.info("[회원 삭제 스케줄러] 유저 ID {} 삭제 완료", userId);
		}
	}
}