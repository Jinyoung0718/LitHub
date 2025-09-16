package com.sjy.LitHub.record.model.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class StudyGroupRecordDTO {

	private Long roomId;

	private String title;

	private String content;

	private Integer totalMinutes;

}