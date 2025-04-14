package com.sjy.LitHub.post.cache.enums;

import java.time.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CachePolicy {

	POPULAR_POST("post:list:popular:page:%d", Duration.ofMinutes(5)),
	SEARCH_POST("post:list:search:%s:page:%d", Duration.ofMinutes(5)),
	TAG_POST("post:list:tag:%s:page:%d", Duration.ofMinutes(5)),
	POST_DETAIL("post:detail:%d:%d", Duration.ofMinutes(30));

	private final String keyFormat;
	private final Duration ttl;

	public String createKey(Object... args) {
		return String.format(keyFormat, args);
	}
}