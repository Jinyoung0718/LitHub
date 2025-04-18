package com.sjy.LitHub.post.cache.scheduler;

import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.post.cache.PostInteractionRedisManager;
import com.sjy.LitHub.post.cache.enums.InteractionType;
import com.sjy.LitHub.post.repository.LikesRepository;
import com.sjy.LitHub.post.repository.ScrapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostInteractionSyncScheduler {

	private final PostInteractionRedisManager redisManager;
	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;

	private static final long SCHEDULE_INTERVAL_MS = 30 * 60 * 1000;

	@Scheduled(fixedRate = SCHEDULE_INTERVAL_MS)
	@Transactional
	public void syncAllPostInteractions() {
		log.info("[Sync] Redis 좋아요/스크랩 → DB 동기화 시작");

		Set<Long> postIds = redisManager.getAllPostIds();
		if (postIds.isEmpty()) {
			log.info("[Sync] 동기화 대상 게시글 없음. 작업 생략");
			return;
		}

		for (Long postId : postIds) {
			try {
				syncInteractionsForPost(postId);
			} catch (Exception e) {
				log.error("[Sync] postId: {} - 동기화 실패: {}", postId, e.getMessage(), e);
			}
		}

		log.info("[Sync] 전체 동기화 작업 완료. 총 {}개 게시글 처리됨", postIds.size());
	}

	private void syncInteractionsForPost(Long postId) {
		Map<InteractionType, Set<Long>> interactionMap = Map.of(
			InteractionType.LIKE, redisManager.getInteractions(postId, InteractionType.LIKE),
			InteractionType.SCRAP, redisManager.getInteractions(postId, InteractionType.SCRAP)
		);

		likesRepository.syncLikes(postId, interactionMap.get(InteractionType.LIKE));
		scrapRepository.syncScraps(postId, interactionMap.get(InteractionType.SCRAP));

		redisManager.clearInteractions(postId);

		log.info("[Sync] postId: {} - 좋아요 {}명, 스크랩 {}명 동기화 완료",
			postId,
			interactionMap.get(InteractionType.LIKE).size(),
			interactionMap.get(InteractionType.SCRAP).size()
		);
	}
}