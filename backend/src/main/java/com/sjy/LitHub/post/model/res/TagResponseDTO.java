package com.sjy.LitHub.post.model.res;

import com.sjy.LitHub.post.entity.Tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagResponseDTO {
	private Long id;
	private String name;

	public static TagResponseDTO from(Tag tag) {
		return new TagResponseDTO(tag.getId(), tag.getName());
	}
}