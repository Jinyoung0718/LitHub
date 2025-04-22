package com.sjy.LitHub.post.cache.keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.PopularZSetKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PopularKeywordManager {

	private final RedisTemplate<String, String> redisTemplate;
	private final SearchCacheAsyncProcessor searchCacheAsyncProcessor;

	private static final int IMMEDIATE_CACHE_THRESHOLD = 100;
	private static final int TOP_N = 10;

	public PopularKeywordManager(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		SearchCacheAsyncProcessor searchCacheAsyncProcessor
	) {
		this.redisTemplate = redisTemplate;
		this.searchCacheAsyncProcessor = searchCacheAsyncProcessor;
	}

	public void onSearch(String keyword) {
		if (!isValid(keyword)) return;

		String normalized = normalize(keyword);
		Double newScore = redisTemplate.opsForZSet()
			.incrementScore(PopularZSetKey.KEYWORDS.getKey(), normalized, 1);

		if (newScore != null && newScore >= IMMEDIATE_CACHE_THRESHOLD) {
			searchCacheAsyncProcessor.cacheSearchResultAsync(normalized, CachePolicy.SEARCH_BY_TITLE);
		}
	}

	public void onTagSearch(String tagName) {
		if (!isValid(tagName)) return;

		String normalized = normalize(tagName);
		Double newScore = redisTemplate.opsForZSet()
			.incrementScore(PopularZSetKey.TAGS.getKey(), normalized, 1);

		if (newScore != null && newScore >= IMMEDIATE_CACHE_THRESHOLD) {
			searchCacheAsyncProcessor.cacheSearchResultAsync(normalized, CachePolicy.SEARCH_BY_TAG);
		}
	}

	public List<String> getRealtimeTopKeywords() {
		return getTopZSet(PopularZSetKey.KEYWORDS);
	}

	public List<String> getRealtimeTopTags() {
		return getTopZSet(PopularZSetKey.TAGS);
	}

	private List<String> getTopZSet(PopularZSetKey keyEnum) {
		Set<String> raw = redisTemplate.opsForZSet().reverseRange(keyEnum.getKey(), 0, TOP_N - 1);
		return raw == null ? List.of() : new ArrayList<>(raw);
	}

	private String normalize(String keyword) {
		return keyword.trim().toLowerCase().replaceAll("\\s+", "_");
	}

	private boolean isValid(String keyword) {
		return keyword != null && !keyword.isBlank();
	}
}