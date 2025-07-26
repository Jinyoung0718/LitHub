package com.sjy.LitHub.post.cache.search;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.SearchPopularZSetKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PopularSearchRecorder {

	private final RedisTemplate<String, String> redisTemplate;
	private static final Duration ZSET_TTL = Duration.ofDays(3);
	private static final int TOP_N = 5;

	public PopularSearchRecorder(@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void onSearch(String keyword) {
		incrementAndExpire(SearchPopularZSetKey.KEYWORDS.getKey(), normalize(keyword));
	}

	public void onTagSearch(String tagName) {
		incrementAndExpire(SearchPopularZSetKey.TAGS.getKey(), normalize(tagName));
	}

	public List<String> getRealtimeTopKeywords() {
		return getTopZSet(SearchPopularZSetKey.KEYWORDS);
	}

	public List<String> getRealtimeTopTags() {
		return getTopZSet(SearchPopularZSetKey.TAGS);
	}

	private void incrementAndExpire(String key, String member) {
		if (!isValid(member)) return;

		redisTemplate.opsForZSet().incrementScore(key, member, 1);

		Long ttl = redisTemplate.getExpire(key);
		if (ttl == null || ttl == -1) {
			redisTemplate.expire(key, ZSET_TTL);
		}
	}

	private List<String> getTopZSet(SearchPopularZSetKey keyEnum) {
		Set<String> raw = redisTemplate.opsForZSet().reverseRange(keyEnum.getKey(), 0, TOP_N - 1);
		return raw == null ? List.of() : new ArrayList<>(raw);
	}

	private String normalize(String keyword) {
		if (!isValid(keyword)) return "";
		return keyword
			.trim()
			.toLowerCase()
			.replaceAll("\\s+", "")
			.replaceAll("[^ㄱ-ㅎ가-힣a-z0-9]", "");
	}

	private boolean isValid(String keyword) {
		return keyword != null && !keyword.isBlank();
	}
}