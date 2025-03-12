package com.sjy.LitHub.record;

import com.sjy.LitHub.account.service.UserInfo.PointService;
import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import com.sjy.LitHub.record.repository.ReadLogRepository;
import com.sjy.LitHub.record.service.ReadLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
@Transactional
@DisplayName("ReadLogService 단위 테스트")
class ReadLogServiceTest {

    @InjectMocks
    private ReadLogService readLogService;

    @Mock
    private ReadLogRepository readLogRepository;

    @Mock
    private PointService pointService;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Test
    @DisplayName("독서 기록 저장 - 성공")
    void saveReadingRecord_Success() {
        int minutes = 30;

        readLogService.saveReadingRecord(userId, minutes);

        verify(readLogRepository, times(1)).upsertReadingLog(userId, LocalDate.now(), minutes);
        verify(pointService, times(1)).updateUserPointsAndTier(userId, minutes);
    }

    @Test
    @DisplayName("독서일 수 조회 - 성공")
    void getReadingStreak_Success() {
        when(readLogRepository.getReadingStreak(userId)).thenReturn(5);

        int result = readLogService.getReadingStreak(userId);

        assertEquals(5, result);
        verify(readLogRepository, times(1)).getReadingStreak(userId);
    }

    @Test
    @DisplayName("월별 독서 통계 조회 - 성공")
    void getMonthlyStatsBatch_Success() {
        int year = 2024;
        Map<Integer, MonthlyReadingStatsResponseDTO> mockStats = new HashMap<>();
        mockStats.put(1, new MonthlyReadingStatsResponseDTO(2024, 1, 300, 10.0, false));
        mockStats.put(2, new MonthlyReadingStatsResponseDTO(2024, 2, 450, 15.0, true));

        when(readLogRepository.getMonthlyStatsBatch(userId, year)).thenReturn(mockStats);

        Map<Integer, MonthlyReadingStatsResponseDTO> result = readLogService.getMonthlyStatsBatch(userId, year);

        assertEquals(2, result.size());
        assertEquals(300, result.get(1).getTotalReadingTime());
        assertEquals(15.0, result.get(2).getAverageReadingTime());

        verify(readLogRepository, times(1)).getMonthlyStatsBatch(userId, year);
    }

    @Test
    @DisplayName("독서 기록 조회 - 성공")
    void getReadingRecords_Success() {
        int year = 2024;
        List<ReadingRecordResponseDTO> mockRecords = Arrays.asList(
                new ReadingRecordResponseDTO(LocalDate.of(2024, 1, 5), 3),
                new ReadingRecordResponseDTO(LocalDate.of(2024, 2, 10), 5)
        );

        when(readLogRepository.getReadingRecords(userId, year)).thenReturn(mockRecords);

        List<ReadingRecordResponseDTO> result = readLogService.getReadingRecords(userId, year);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 1, 5), result.get(0).getDate());
        assertEquals(5, result.get(1).getColorLevel());

        verify(readLogRepository, times(1)).getReadingRecords(userId, year);
    }

    @Test
    @DisplayName("연속 독서일 초기화 - 성공")
    void resetStreakForInactiveUsers_Success() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        readLogService.resetStreakForInactiveUsers();
        verify(readLogRepository, times(1)).resetStreakForInactiveUsers(yesterday, today);
    }
}