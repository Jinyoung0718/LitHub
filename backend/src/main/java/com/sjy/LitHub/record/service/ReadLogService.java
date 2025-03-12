package com.sjy.LitHub.record.service;

import com.sjy.LitHub.account.service.UserInfo.PointService;
import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import com.sjy.LitHub.record.repository.ReadLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadLogService {

    private final ReadLogRepository readLogRepository;
    private final PointService pointService;

    @Transactional
    public void saveReadingRecord(Long userId, int minutes) {
        readLogRepository.upsertReadingLog(userId, LocalDate.now(), minutes);
        pointService.updateUserPointsAndTier(userId, minutes);
    }

    @Transactional(readOnly = true)
    public Integer getReadingStreak(Long userId) {
        return readLogRepository.getReadingStreak(userId);
    }

    @Transactional(readOnly = true)
    public Map<Integer, MonthlyReadingStatsResponseDTO> getMonthlyStatsBatch(Long userId, int year) {
        return readLogRepository.getMonthlyStatsBatch(userId, year);
    }

    @Transactional(readOnly = true)
    public List<ReadingRecordResponseDTO> getReadingRecords(Long userId, int year) {
        return readLogRepository.getReadingRecords(userId, year);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetStreakForInactiveUsers() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        readLogRepository.resetStreakForInactiveUsers(yesterday, today);
    }
}