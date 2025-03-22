package com.sjy.LitHub.record.model.queryresult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReadingStatsResult {

	private int month;

	private int totalReadingTime;

	private int readingCount;

	private double averageReadingTime;

}