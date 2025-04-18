package com.sjy.LitHub.post.model.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadImageResponseDTO {
	private String url;
	private String fileName;
}