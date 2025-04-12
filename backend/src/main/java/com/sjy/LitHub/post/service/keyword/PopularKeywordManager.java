package com.sjy.LitHub.post.service.keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PopularKeywordManager {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String KEYWORD_ZSET_KEY = "search:keywords";
	private static final int TOP_N = 10;

	public PopularKeywordManager(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// 검색 시 호출 - ZSet score 증가
	public void onSearch(String keyword) {
		if (keyword != null && !keyword.isBlank()) {
			String normalized = normalize(keyword);
			redisTemplate.opsForZSet().incrementScore(KEYWORD_ZSET_KEY, normalized, 1);
		}
	}

	// 실시간 인기 검색어 조회
	public List<String> getRealtimeTopKeywords() {
		Set<String> raw = redisTemplate.opsForZSet().reverseRange(KEYWORD_ZSET_KEY, 0, TOP_N - 1);
		if (raw == null) return List.of();
		return new ArrayList<>(raw);
	}

	private String normalize(String keyword) {
		return keyword.trim().toLowerCase().replaceAll("\\s+", "_");
	}
}