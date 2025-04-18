package com.sjy.LitHub.post.model.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostContentUpdateDTO {
	private String title;
	private String contentMarkdown;
}