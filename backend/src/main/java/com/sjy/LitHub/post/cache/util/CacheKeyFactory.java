package com.sjy.LitHub.post.cache.util;

import org.springframework.stereotype.Component;

@Component
public class CacheKeyFactory {

	public String postDetailKey(Long postId, Long userId) {
		return String.format("post:detail:%d:%d", postId, userId);
	} // 게시글 상세 보기 캐시 키

	public String popularPostListKey(int page) {
		return String.format("post:list:popular:page:%d", page);
	} // 인기글 목록 페이지별 캐시 키

	public String searchPostListKey(String keyword, int page) {
		return String.format("post:list:search:%s:page:%d", sanitize(keyword), page);
	} // 검색 결과 페이지 캐시 키

	private String sanitize(String keyword) {
		return keyword.trim().replaceAll("\\s+", "_").toLowerCase();
	}
}