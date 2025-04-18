package com.sjy.LitHub.record.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.repository.readLogStatus.ReadLogStatsRepository;

@ExtendWith(MockitoExtension.class)
class ReadLogStatusServiceTest {

	@InjectMocks
	private ReadLogStatusService readLogStatusService;

	@Mock
	private ReadLogStatsRepository readLogStatsRepository;

	private final Long userId = 1L;
	private final int year = 2025;
	private final int month = 3;
	private final LocalDate date = LocalDate.of(year, month, 15);

	@Test
	@DisplayName("1. updateReadingStats - 읽기 통계 upsert 호출 확인")
	void testUpdateReadingStats() {
		int readingTime = 45;
		readLogStatusService.updateReadingStats(userId, date, readingTime);

		verify(readLogStatsRepository).upsertReadingStats(eq(userId), eq(year), eq(month), eq(readingTime));
	}

	@Test
	@DisplayName("2. getReadingStats - 사용자 통계 조회")
	void testGetReadingStats() {
		ReadingStatsResponseDTO mockResponse = mock(ReadingStatsResponseDTO.class);
		when(readLogStatsRepository.getReadingStats(userId, year)).thenReturn(mockResponse);

		ReadingStatsResponseDTO result = readLogStatusService.getReadingStats(userId, year);

		assertEquals(mockResponse, result);
		verify(readLogStatsRepository).getReadingStats(userId, year);
	}
}
