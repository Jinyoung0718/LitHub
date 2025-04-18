package com.sjy.LitHub.post.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.PopularZSetKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PopularKeywordManager {

	private final RedisTemplate<String, String> redisTemplate;
	private static final int TOP_N = 10;

	public PopularKeywordManager(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void onSearch(String keyword) {
		if (isValid(keyword)) {
			String normalized = normalize(keyword);
			redisTemplate.opsForZSet()
				.incrementScore(PopularZSetKey.KEYWORDS.getKey(), normalized, 1);
		}
	}

	public void onTagSearch(String tagName) {
		if (isValid(tagName)) {
			String normalized = normalize(tagName);
			redisTemplate.opsForZSet()
				.incrementScore(PopularZSetKey.TAGS.getKey(), normalized, 1);
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