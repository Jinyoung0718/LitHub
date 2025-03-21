package com.sjy.LitHub.record.model;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class MonthlyReadingStatsResponseDTO {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final int year;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final int month;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final int totalReadingTime;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final int readingCount;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final double averageReadingTime;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final boolean isMostFrequent;

	public static MonthlyReadingStatsResponseDTO from(ReadingStatsQueryResult result, int year, Set<Integer> mostFrequentMonths) {
		return MonthlyReadingStatsResponseDTO.builder()
			.year(year)
			.month(result.getMonth())
			.totalReadingTime(result.getTotalReadingTime())
			.averageReadingTime(result.getAverageReadingTime())
			.readingCount(result.getReadingCount())
			.isMostFrequent(mostFrequentMonths.contains(result.getMonth()))
			.build();
	}
}