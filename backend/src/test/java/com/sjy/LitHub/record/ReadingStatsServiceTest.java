package com.sjy.LitHub.record;

import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.service.ReadLogService;
import com.sjy.LitHub.record.service.ReadingStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class ReadingStatsServiceTest {

    @InjectMocks
    private ReadingStatsService readingStatsService;

    @Mock
    private ReadLogService readLogService;

    private Long userId;
    private Integer mockReadingStreak;
    private Map<Integer, MonthlyReadingStatsResponseDTO> mockMonthlyStats;
    private List<ReadingRecordResponseDTO> mockReadingRecords;

    @BeforeEach
    void setUp() {
        userId = 1L;
        mockReadingStreak = 5;

        mockMonthlyStats = new HashMap<>();
        mockMonthlyStats.put(1, new MonthlyReadingStatsResponseDTO(2024, 1, 10, 5.5, true));
        mockMonthlyStats.put(2, new MonthlyReadingStatsResponseDTO(2024, 2, 15, 7.2, false));

        mockReadingRecords = Arrays.asList(
                new ReadingRecordResponseDTO(LocalDate.of(2024, 1, 5), 3),
                new ReadingRecordResponseDTO(LocalDate.of(2024, 2, 10), 5)
        );

        when(readLogService.getReadingStreak(userId)).thenReturn(mockReadingStreak);
        when(readLogService.getMonthlyStatsBatch(userId, LocalDate.now().getYear())).thenReturn(mockMonthlyStats);
        when(readLogService.getReadingRecords(userId, LocalDate.now().getYear())).thenReturn(mockReadingRecords);
    }

    @Test
    @DisplayName("독서 통계 조회 성공")
    void getReadingStats_Success() {
        ReadingStatsResponseDTO response = readingStatsService.getReadingStats(userId);

        assertNotNull(response);
        assertEquals(mockReadingStreak, response.getReadingStreak());
        assertEquals(mockMonthlyStats.size(), response.getMonthlyStats().size());
        assertEquals(mockReadingRecords.size(), response.getReadingRecords().size());

        verify(readLogService, times(1)).getReadingStreak(userId);
        verify(readLogService, times(1)).getMonthlyStatsBatch(userId, LocalDate.now().getYear());
        verify(readLogService, times(1)).getReadingRecords(userId, LocalDate.now().getYear());
    }

    @Test
    @DisplayName("독서 연속일이 없을 때 기본값(0) 반환")
    void getReadingStats_NoStreak() {
        when(readLogService.getReadingStreak(userId)).thenReturn(null);

        ReadingStatsResponseDTO response = readingStatsService.getReadingStats(userId);

        assertNotNull(response);
        assertEquals(0, response.getReadingStreak());
    }
}