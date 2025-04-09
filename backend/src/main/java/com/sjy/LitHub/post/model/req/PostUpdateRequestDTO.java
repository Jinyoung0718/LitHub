package com.sjy.LitHub.post.model.req;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUpdateRequestDTO {
	private String title;
	private String contentMarkdown;
	private MultipartFile thumbnail;
}