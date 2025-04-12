package com.sjy.LitHub.post.service.keyword;

import java.time.Duration;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.util.CacheKeyFactory;
import com.sjy.LitHub.post.cache.PostListCacheManager;
import com.sjy.LitHub.post.model.res.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularKeywordPostPreheatScheduler {

	private final PopularKeywordManager popularKeywordManager;
	private final PostRepository postRepository;
	private final PostListCacheManager postListCacheManager;
	private final CacheKeyFactory cacheKeyFactory;

	private static final int PAGE = 0;
	private static final int SIZE = 10;

	@Scheduled(cron = "0 */10 * * * ?")
	public void preheat() {
		List<String> keywords = popularKeywordManager.getRealtimeTopKeywords();
		Pageable pageable = PageRequest.of(PAGE, SIZE);

		for (String keyword : keywords) {
			String key = cacheKeyFactory.searchPostListKey(keyword, PAGE);

			try {
				Page<PostSummaryResponseDTO> result = postRepository.searchByKeyword(keyword, pageable);
				postListCacheManager.save(key, result, Duration.ofMinutes(5));
				log.info("[검색어 목록 캐시] '{}' - 저장 완료", keyword);
			} catch (Exception e) {
				log.warn("[검색어 목록 캐시] '{}' - 저장 실패: {}", keyword, e.getMessage());
			}
		}
	}
}