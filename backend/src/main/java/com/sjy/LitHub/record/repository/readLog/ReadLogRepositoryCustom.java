package com.sjy.LitHub.record.repository.readLog;

import java.time.LocalDate;

public interface ReadLogRepositoryCustom {

    // 독서 기록 업데이트 및 생성 (업설트)
    void saveOrUpdateReadLog(Long userId, LocalDate date, int minutes);

    // 독서 streak 초기화
    void resetStreakForInactiveUsers(LocalDate yesterday, LocalDate today);
}