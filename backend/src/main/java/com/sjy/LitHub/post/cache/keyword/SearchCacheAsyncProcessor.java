package com.sjy.LitHub.post.cache.keyword;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.post.PostListCacheManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchCacheAsyncProcessor {

	private final PostRepository postRepository;
	private final PostListCacheManager postListCacheManager;

	private static final int PAGE = 0;
	private static final int SIZE = 10;

	@Async("searchExecutor")
	public void cacheSearchResultAsync(String keyword, CachePolicy policy) {
		try {
			Pageable pageable = PageRequest.of(PAGE, SIZE);
			Page<PostSummaryResponseDTO> result = switch (policy) {
				case SEARCH_BY_TITLE -> postRepository.searchByTitle(keyword, pageable);
				case SEARCH_BY_TAG -> postRepository.searchByTag(keyword, pageable);
				default -> throw new IllegalArgumentException("지원하지 않는 캐시 정책입니다: " + policy);
			};

			postListCacheManager.save(policy, result, keyword, PAGE);
			log.info("[즉시 캐시] '{}' - {} 저장 완료", keyword, policy.name());
		} catch (Exception e) {
			log.warn("[즉시 캐시] '{}' - {} 저장 실패: {}", keyword, policy.name(), e.getMessage());
		}
	}
}