package com.sjy.LitHub.post.cache.interaction;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.InteractionType;
import com.sjy.LitHub.post.repository.like.LikesRepository;
import com.sjy.LitHub.post.repository.scrap.ScrapRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InteractionStateManager {

	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;
	private final InteractionCacheUtils interactionCacheUtils;

	public InteractionStateManager(
		LikesRepository likesRepository,
		ScrapRepository scrapRepository,
		InteractionCacheUtils interactionCacheUtils
	) {
		this.likesRepository = likesRepository;
		this.scrapRepository = scrapRepository;
		this.interactionCacheUtils = interactionCacheUtils;
	}

	public void onLike(Long postId, Long userId) {
		String userKey = userId.toString();
		String setKey = CachePolicy.POST_LIKES_USERS.createKey(postId);
		String countKey = CachePolicy.POST_LIKES_COUNT.createKey(postId);

		interactionCacheUtils.addToSet(setKey, userKey); // userId를 set에 추가
		interactionCacheUtils.incrementIfExists(countKey); // 카운트 증가
		interactionCacheUtils.updatePopularity(postId, InteractionType.LIKE); // 점수 획득
		interactionCacheUtils.deleteInteractionCache(postId, userId);
	}

	public void onUnlike(Long postId, Long userId) {
		String userKey = userId.toString();
		String setKey = CachePolicy.POST_LIKES_USERS.createKey(postId);
		String countKey = CachePolicy.POST_LIKES_COUNT.createKey(postId);

		interactionCacheUtils.removeFromSet(setKey, userKey); // userId를 set에서 제거
		interactionCacheUtils.decrementIfExists(countKey); // 카운트 차감
		interactionCacheUtils.updatePopularity(postId, InteractionType.UNLIKE); // 점수 차감
		interactionCacheUtils.deleteInteractionCache(postId, userId);
	}

	public void onScrap(Long postId, Long userId) {
		String userKey = userId.toString();
		String setKey = CachePolicy.POST_SCRAPS_USERS.createKey(postId);
		String countKey = CachePolicy.POST_SCRAPS_COUNT.createKey(postId);

		interactionCacheUtils.addToSet(setKey, userKey); // userId를 set에 추가
		interactionCacheUtils.incrementIfExists(countKey); // 카운트 증가
		interactionCacheUtils.updatePopularity(postId, InteractionType.SCRAP); // 점수 획득
		interactionCacheUtils.deleteInteractionCache(postId, userId);
	}

	public void onUnscrap(Long postId, Long userId) {
		String userKey = userId.toString();
		String setKey = CachePolicy.POST_SCRAPS_USERS.createKey(postId);
		String countKey = CachePolicy.POST_SCRAPS_COUNT.createKey(postId);

		interactionCacheUtils.removeFromSet(setKey, userKey); // userId를 set에서 제거
		interactionCacheUtils.decrementIfExists(countKey); // 카운트 차감
		interactionCacheUtils.updatePopularity(postId, InteractionType.UNSCRAP); // 점수 차감
		interactionCacheUtils.deleteInteractionCache(postId, userId);
	}

	public PostInteractionState resolve(Long postId, Long userId) {
		String cacheKey = interactionCacheUtils.createInteractionKey(postId, userId);
		PostInteractionState cached = interactionCacheUtils.getInteractionState(cacheKey);
		if (cached != null) return cached;

		String userKey = userId.toString();

		boolean liked = interactionCacheUtils.isMember(CachePolicy.POST_LIKES_USERS.createKey(postId), userKey);
		long likeCount = interactionCacheUtils.getCount(CachePolicy.POST_LIKES_COUNT.createKey(postId));

		boolean scrapped = interactionCacheUtils.isMember(CachePolicy.POST_SCRAPS_USERS.createKey(postId), userKey);
		long scrapCount = interactionCacheUtils.getCount(CachePolicy.POST_SCRAPS_COUNT.createKey(postId));

		if (!liked && likeCount == 0) {
			liked = likesRepository.existsByPostIdAndUserId(postId, userId);
			likeCount = likesRepository.countByPostId(postId);
		}
		if (!scrapped && scrapCount == 0) {
			scrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);
			scrapCount = scrapRepository.countByPostId(postId);
		}

		PostInteractionState state = new PostInteractionState(liked, likeCount, scrapped, scrapCount);
		interactionCacheUtils.putInteractionCache(postId, userId, state);
		return state;
	}

	public List<Long> getTopPostIds(int size) {
		return interactionCacheUtils.getTopPostIds(size);
	}
}