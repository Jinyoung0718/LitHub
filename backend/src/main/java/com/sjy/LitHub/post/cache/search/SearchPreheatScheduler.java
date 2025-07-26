package com.sjy.LitHub.post.cache.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchPreheatScheduler {

	private final PopularSearchRecorder popularSearchRecorder;
	private final PostRepository postRepository;
	private final SearchResultCacheStore searchResultCacheStore;

	private static final int PAGE = 0;
	private static final int SIZE = 10;

	@Scheduled(cron = "0 */5 * * * ?")
	public void preheat() {
		Pageable pageable = PageRequest.of(PAGE, SIZE);

		popularSearchRecorder.getRealtimeTopKeywords().forEach(keyword -> {
			try {
				Page<PostSummaryResponseDTO> result = postRepository.searchByTitle(keyword, pageable);
				searchResultCacheStore.save(CachePolicy.SEARCH_BY_TITLE, result, keyword, PAGE);
				log.info("[Preheat] 인기 검색어 '{}' 검색 결과 캐시 완료", keyword);
			} catch (Exception e) {
				log.warn("[Preheat] 인기 검색어 '{}' 검색 결과 캐시 실패: {}", keyword, e.getMessage());
			}
		});

		popularSearchRecorder.getRealtimeTopTags().forEach(tag -> {
			try {
				Page<PostSummaryResponseDTO> result = postRepository.searchByTag(tag, pageable);
				searchResultCacheStore.save(CachePolicy.SEARCH_BY_TAG, result, tag, PAGE);
				log.info("[Preheat] 인기 태그 '{}' 검색 결과 캐시 완료", tag);
			} catch (Exception e) {
				log.warn("[Preheat] 인기 태그 '{}' 검색 결과 캐시 실패: {}", tag, e.getMessage());
			}
		});
	}
}