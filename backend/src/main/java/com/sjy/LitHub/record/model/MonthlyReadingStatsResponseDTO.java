package com.sjy.LitHub.record.model;

import java.util.Set;

import com.sjy.LitHub.record.model.queryresult.MonthlyReadingStatsResult;

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
	private final int totalReadingTime; // 해당 월의 총 독서 시간 (분 단위)

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final int readingCount; // 해당 월의 독서 기록을 남긴 날짜 수

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final double averageReadingTime; // 해당 월의 평균 독서 시간

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final boolean isMostFrequent; // 가장 많이 읽은 달인지 여부

	public static MonthlyReadingStatsResponseDTO from(MonthlyReadingStatsResult result, int year, Set<Integer> mostFrequentMonths) {
		return MonthlyReadingStatsResponseDTO.builder()
			.year(year)
			.month(result.getMonth())
			.totalReadingTime(result.getTotalReadingTime())
			.averageReadingTime(result.getAverageReadingTime())
			.readingCount(result.getReadingCount())
			.isMostFrequent(mostFrequentMonths.contains(result.getMonth()))
			.build();
	}

	public static MonthlyReadingStatsResponseDTO empty(int year, int month) {
		return MonthlyReadingStatsResponseDTO.builder()
			.year(year)
			.month(month)
			.totalReadingTime(0)
			.readingCount(0)
			.averageReadingTime(0.0)
			.isMostFrequent(false)
			.build();
	}
}