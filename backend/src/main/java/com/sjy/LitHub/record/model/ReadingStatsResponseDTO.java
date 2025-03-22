package com.sjy.LitHub.record.model;

import java.util.Collections;
import java.util.List;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class ReadingStatsResponseDTO {

	@NonNull
	private final int readingStreak;

	@NonNull
	@Builder.Default
	private final List<ReadingRecordResponseDTO> readingRecords = Collections.emptyList();

	@NonNull
	@Builder.Default
	private final List<MonthlyReadingStatsResponseDTO> monthlyStats = Collections.emptyList();

	public static ReadingStatsResponseDTO of(int readingStreak, List<MonthlyReadingStatsResponseDTO> monthlyStats, List<ReadingRecordResponseDTO> readingRecords) {
		return ReadingStatsResponseDTO.builder()
			.readingStreak(readingStreak)
			.monthlyStats(monthlyStats != null ? monthlyStats : Collections.emptyList())
			.readingRecords(readingRecords != null ? readingRecords : Collections.emptyList())
			.build();
	}
}