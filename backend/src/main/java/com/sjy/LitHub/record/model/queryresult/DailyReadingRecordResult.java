package com.sjy.LitHub.record.model.queryresult;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyReadingRecordResult {

	private LocalDate date;

	private int streak;

	private int colorLevel;

}