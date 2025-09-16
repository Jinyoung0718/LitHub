package com.sjy.LitHub.global.message.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostDeletedEvent {
	private Long postId;
	private Long authorId;
}