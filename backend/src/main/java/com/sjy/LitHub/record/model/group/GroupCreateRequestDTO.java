package com.sjy.LitHub.record.model.group;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequestDTO {

	@NotBlank(message = "그룹 제목은 필수입니다.")
	private String title;

	@NotBlank(message = "그룹 설명은 필수입니다.")
	private String content;
}