package com.sjy.LitHub.post.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponseDTO {
	private boolean liked;
	private long likeCount;
}