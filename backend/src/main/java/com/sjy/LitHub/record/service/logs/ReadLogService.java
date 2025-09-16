package com.sjy.LitHub.record.service.logs;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.service.UserInfo.PointService;
import com.sjy.LitHub.record.repository.readLog.ReadLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadLogService {

    private final ReadLogRepository readLogRepository;
    private final ReadLogStatusService readLogStatusService;
    private final PointService pointService;

    @Transactional
    public void saveReadingRecord(Long userId, int minutes) {
        LocalDate today = LocalDate.now();
        readLogRepository.saveOrUpdateReadLog(userId, today, minutes);
        readLogStatusService.updateReadingStats(userId, today, minutes);
        pointService.updateUserPointsAndTier(userId, minutes);
    } // 사용자가 읽은 시간을 기록

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetStreakForInactiveUsers() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        readLogRepository.resetStreakForInactiveUsers(yesterday, today);
    } // 사용자의 읽기 통계 업데이트
}