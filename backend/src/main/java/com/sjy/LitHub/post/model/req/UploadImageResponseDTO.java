package com.sjy.LitHub.post.model.req;

import com.sjy.LitHub.file.entity.PostGenFile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadImageResponseDTO {

	private String url;

	private String storageKey;

	public static UploadImageResponseDTO from(PostGenFile image) {
		return new UploadImageResponseDTO(image.getPublicUrl(), image.getStorageKey());
	}
}