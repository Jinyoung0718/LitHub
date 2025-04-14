package com.sjy.LitHub.post.cache.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PopularZSetKey {
	KEYWORDS("search:keywords"),
	TAGS("search:tags");

	private final String key;
}