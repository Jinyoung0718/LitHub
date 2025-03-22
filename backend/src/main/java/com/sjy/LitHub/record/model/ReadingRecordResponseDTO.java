package com.sjy.LitHub.record.model;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

import com.sjy.LitHub.record.model.queryresult.DailyReadingRecordResult;

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
public class ReadingRecordResponseDTO {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	@NonNull
	private final LocalDate date;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private final int colorLevel;

	public static ReadingRecordResponseDTO from(DailyReadingRecordResult result) {
		return ReadingRecordResponseDTO.builder()
			.date(result.getDate())
			.colorLevel(result.getColorLevel())
			.build();
	}
}