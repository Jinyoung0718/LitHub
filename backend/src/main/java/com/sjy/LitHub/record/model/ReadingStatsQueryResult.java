package com.sjy.LitHub.record.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadingStatsQueryResult {

	// 최고 연속 기록
	private int readingStreak;

	private LocalDate date;

	private Integer colorLevel;

	private int month;

	private int totalReadingTime;

	private int readingCount;

	private double averageReadingTime;
}