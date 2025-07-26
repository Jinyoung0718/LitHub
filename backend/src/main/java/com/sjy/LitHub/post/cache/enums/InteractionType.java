package com.sjy.LitHub.post.cache.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionType {

	LIKE(1),      // 좋아요 +1점
	UNLIKE(-1),   // 좋아요 취소 -1점
	SCRAP(2),     // 스크랩 +2점
	UNSCRAP(-2);  // 스크랩 취소 -2점

	private final int weight;
}