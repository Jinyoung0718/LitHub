package com.sjy.LitHub.post.cache.scheduler;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularPostIdSyncScheduler {

	private final PostRepository postRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private static final int TOP_N = 30;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void syncPopularPostIds() {
		log.info("[PopularPost] 인기 게시글 ID Set 갱신 시작");

		try {
			List<Long> topPostIds = postRepository.findTopPopularPostIds(PageRequest.of(0, TOP_N));
			if (topPostIds.isEmpty()) {
				log.info("[PopularPost] 인기 게시글 없음. 작업 생략");
				return;
			}

			// Redis Set 초기화 및 갱신
			redisTemplate.delete(CachePolicy.POPULAR_POST_SET_KEY);
			topPostIds.forEach(id -> redisTemplate.opsForSet().add(CachePolicy.POPULAR_POST_SET_KEY, id.toString()));

			log.info("[PopularPost] 총 {}개의 인기 게시글 ID 갱신 완료", topPostIds.size());
		} catch (Exception e) {
			log.error("[PopularPost] 인기 게시글 ID 갱신 실패: {}", e.getMessage(), e);
		}
	}
}