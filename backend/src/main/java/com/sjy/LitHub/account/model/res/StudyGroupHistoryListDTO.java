package com.sjy.LitHub.account.model.res;

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
public class StudyGroupHistoryListDTO {

	@NonNull
	private final List<StudyGroupHistoryDTO> items;

	public static StudyGroupHistoryListDTO of(List<StudyGroupHistoryDTO> items) {
		return StudyGroupHistoryListDTO.builder().items(items).build();
	}
}