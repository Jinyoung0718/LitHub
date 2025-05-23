package com.sjy.LitHub.post.cache.scheduler;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.keyword.PopularKeywordManager;
import com.sjy.LitHub.post.cache.post.PostListCacheManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularKeywordPostPreheatScheduler {

	private final PopularKeywordManager popularKeywordManager;
	private final PostListCacheManager postListCacheManager;
	private final PostRepository postRepository;

	private static final int PAGE = 0;
	private static final int SIZE = 10;

	@Scheduled(cron = "0 */5 * * * ?")
	public void preheat() {
		Pageable pageable = PageRequest.of(PAGE, SIZE);

		List<String> keywords = popularKeywordManager.getRealtimeTopKeywords();
		for (String keyword : keywords) {
			try {
				Page<PostSummaryResponseDTO> result = postRepository.searchByTitle(keyword, pageable); // 제목 기반 검색
				postListCacheManager.save(CachePolicy.SEARCH_BY_TITLE, result, keyword, PAGE);
				log.info("[제목 검색어 목록 캐시] '{}' - 저장 완료", keyword);
			} catch (Exception e) {
				log.warn("[제목 검색어 목록 캐시] '{}' - 저장 실패: {}", keyword, e.getMessage());
			}
		}

		List<String> tags = popularKeywordManager.getRealtimeTopTags();
		for (String tag : tags) {
			try {
				Page<PostSummaryResponseDTO> result = postRepository.searchByTag(tag, pageable);
				postListCacheManager.save(CachePolicy.SEARCH_BY_TAG, result, tag, PAGE);
				log.info("[태그 목록 캐시] '{}' - 저장 완료", tag);
			} catch (Exception e) {
				log.warn("[태그 목록 캐시] '{}' - 저장 실패: {}", tag, e.getMessage());
			}
		}
	}
}