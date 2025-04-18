package com.sjy.LitHub.post.model.req;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDTO {

	@NotBlank(message = "제목은 비워둘 수 없습니다.")
	@Size(min = 2, max = 100, message = "제목은 2자 이상, 100자 이하로 입력해주세요.")
	private String title;

	@NotBlank(message = "내용은 반드시 입력해야 합니다.")
	@Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다.")
	private String contentMarkdown;

	@NotNull(message = "태그 리스트는 null 일 수 없습니다. 빈 리스트라도 전달해주세요.")
	@Size(max = 10, message = "태그는 최대 10개까지만 등록할 수 있습니다.")
	private List<@NotBlank @Size(max = 20) String> tags = new ArrayList<>();

	@NotBlank(message = "썸네일 URL 은 필수입니다.")
	private String thumbnailFileName;
}