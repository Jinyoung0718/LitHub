package com.sjy.LitHub.record.service;

import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadingStatsService {

    private final ReadLogService readLogService;

    @Transactional(readOnly = true)
    public ReadingStatsResponseDTO getReadingStats(Long userId) {
        Integer readingStreak = readLogService.getReadingStreak(userId);
        Map<Integer, MonthlyReadingStatsResponseDTO> monthlyStatsMap = readLogService.getMonthlyStatsBatch(userId, LocalDate.now().getYear());
        List<ReadingRecordResponseDTO> readingRecords = readLogService.getReadingRecords(userId, LocalDate.now().getYear());

        return ReadingStatsResponseDTO.builder()
                .readingStreak(readingStreak != null ? readingStreak : 0)
                .monthlyStats(new ArrayList<>(monthlyStatsMap.values()))
                .readingRecords(readingRecords)
                .build();
    }
}