package com.sjy.LitHub.record.repository;

import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReadLogRepositoryCustom {

    // 연속 독서 일수 조회
    Integer getReadingStreak(Long userId);

    // 월별 독서 통계 조회
    Map<Integer, MonthlyReadingStatsResponseDTO> getMonthlyStatsBatch(Long userId, int year);

    // 1년치 독서 기록 조회
    List<ReadingRecordResponseDTO> getReadingRecords(Long userId, int year);

    // 독서 기록 추가 또는 업데이트
    void upsertReadingLog(Long userId, LocalDate date, int minutes);

    // 독서 streak 초기화
    void resetStreakForInactiveUsers(LocalDate yesterday, LocalDate today);
}