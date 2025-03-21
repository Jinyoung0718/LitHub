package com.sjy.LitHub.record.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.repository.ReadLogStatus.ReadLogStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadLogStatusService {

    private final ReadLogStatsRepository readLogStatsRepository;

    @Transactional
    public void updateReadingStats(Long userId, LocalDate date, int newReadingTime) {
        readLogStatsRepository.upsertReadingStats(userId, date.getYear(), date.getMonthValue(), newReadingTime);
    } // 사용자의 읽기 통계 업데이트

    @Transactional(readOnly = true)
    public ReadingStatsResponseDTO getReadingStats(Long userId, int year) {
        return readLogStatsRepository.getReadingStats(userId, year);
    } // 사용자의 특정 연도에 대한 읽기 통계를 가져오는 메소드
}