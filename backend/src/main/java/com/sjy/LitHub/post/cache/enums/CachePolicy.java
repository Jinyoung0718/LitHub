package com.sjy.LitHub.post.cache.enums;

import java.time.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CachePolicy {

	// 인터랙션 Set
	POST_LIKES_USERS("post:likes:%d:users", Duration.ofDays(7)),
	POST_SCRAPS_USERS("post:scraps:%d:users", Duration.ofDays(7)),

	POST_INTERACTION_STATE("post:interaction:%d:%d", Duration.ofMinutes(10)),

	// 인터랙션 Count
	POST_LIKES_COUNT("post:likes:%d:count", Duration.ofDays(7)),
	POST_SCRAPS_COUNT("post:scraps:%d:count", Duration.ofDays(7)),

	// 검색/피드
	SEARCH_BY_TITLE("post:list:search:title:%s:page:%s", Duration.ofMinutes(30)),
	SEARCH_BY_TAG("post:list:search:tag:%s:page:%s", Duration.ofMinutes(30)),
	FEED_POST("feed:%d", Duration.ZERO),

	// 인기글 ZSet
	POPULAR_POST_ZSET("post:popular:zset", Duration.ofHours(6)),

	// 상세 캐시 (인기글은 6시간, 비인기글은 30분)
	POST_DETAIL_NON_POPULAR("post:detail:non-popular:%d", Duration.ofMinutes(30)),
	POST_DETAIL_POPULAR("post:detail:popular:%d", Duration.ofHours(6)),

	// 검색 캐싱 역색인 추적용 set 키
	RELATED_SEARCH_CACHE_KEYS("post:related:cachekeys:%d", Duration.ZERO);


	private final String keyFormat;
	private final Duration ttl;

	public String createKey(Object... args) {
		return String.format(keyFormat, args);
	}
}