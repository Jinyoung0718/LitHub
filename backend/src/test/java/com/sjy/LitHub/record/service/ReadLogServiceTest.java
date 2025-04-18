package com.sjy.LitHub.record.service;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sjy.LitHub.account.service.UserInfo.PointService;
import com.sjy.LitHub.record.repository.readLog.ReadLogRepository;

@ExtendWith(MockitoExtension.class)
class ReadLogServiceTest {

	@InjectMocks
	private ReadLogService readLogService;

	@Mock
	private ReadLogRepository readLogRepository;

	@Mock
	private ReadLogStatusService readLogStatusService;

	@Mock
	private PointService pointService;

	private final LocalDate today = LocalDate.now();

	@Test
	@DisplayName("1. saveReadingRecord - 독서 기록 저장 및 통계, 포인트 업데이트")
	void testSaveReadingRecord() {
		Long userId = 1L;
		int minutes = 30;
		readLogService.saveReadingRecord(userId, minutes);

		verify(readLogRepository).saveOrUpdateReadLog(eq(userId), eq(today), eq(minutes));
		verify(readLogStatusService).updateReadingStats(eq(userId), eq(today), eq(minutes));
		verify(pointService).updateUserPointsAndTier(eq(userId), eq(minutes));
	}

	@Test
	@DisplayName("2. resetStreakForInactiveUsers - 비활성 사용자 streak 초기화")
	void testResetStreakForInactiveUsers() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate today = LocalDate.now();

		readLogService.resetStreakForInactiveUsers();

		verify(readLogRepository).resetStreakForInactiveUsers(eq(yesterday), eq(today));
	}
}
