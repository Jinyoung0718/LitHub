package com.sjy.LitHub.post.cache.interaction;

import org.springframework.stereotype.Service;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.InteractionType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InteractionWriteService {

	private final InteractionCacheStore cacheStore;

	public void onLike(Long postId, Long userId) {
		cacheStore.addToSet(CachePolicy.POST_LIKES_USERS.createKey(postId), userId.toString());
		cacheStore.increment(CachePolicy.POST_LIKES_COUNT.createKey(postId));
		cacheStore.updatePopularity(postId, InteractionType.LIKE);
		cacheStore.deleteInteractionCache(postId, userId);
	}

	public void onUnlike(Long postId, Long userId) {
		cacheStore.removeFromSet(CachePolicy.POST_LIKES_USERS.createKey(postId), userId.toString());
		cacheStore.safeDecrement(CachePolicy.POST_LIKES_COUNT.createKey(postId));
		cacheStore.updatePopularity(postId, InteractionType.UNLIKE);
		cacheStore.deleteInteractionCache(postId, userId);
	}

	public void onScrap(Long postId, Long userId) {
		cacheStore.addToSet(CachePolicy.POST_SCRAPS_USERS.createKey(postId), userId.toString());
		cacheStore.increment(CachePolicy.POST_SCRAPS_COUNT.createKey(postId));
		cacheStore.updatePopularity(postId, InteractionType.SCRAP);
		cacheStore.deleteInteractionCache(postId, userId);
	}

	public void onUnscrap(Long postId, Long userId) {
		cacheStore.removeFromSet(CachePolicy.POST_SCRAPS_USERS.createKey(postId), userId.toString());
		cacheStore.safeDecrement(CachePolicy.POST_SCRAPS_COUNT.createKey(postId));
		cacheStore.updatePopularity(postId, InteractionType.UNSCRAP);
		cacheStore.deleteInteractionCache(postId, userId);
	}
}
