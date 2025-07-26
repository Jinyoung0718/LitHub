package com.sjy.LitHub.post.cache.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchPopularZSetKey {

	KEYWORDS("post:search:keywords:zset"),
	TAGS("post:search:tags:zset");

	private final String key;
}