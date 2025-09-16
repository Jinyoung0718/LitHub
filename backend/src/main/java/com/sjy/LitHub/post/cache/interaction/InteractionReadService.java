package com.sjy.LitHub.post.cache.interaction;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.repository.like.LikesRepository;
import com.sjy.LitHub.post.repository.scrap.ScrapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InteractionReadService {

	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;
	private final InteractionCacheStore cacheStore;

	public PostInteractionState resolveForViewer(Long postId, Long viewerUserId) {
		PostInteractionState cached = cacheStore.getInteractionState(postId, viewerUserId);
		if (cached != null) return cached;

		boolean liked = cacheStore.isMember(CachePolicy.POST_LIKES_USERS.createKey(postId), viewerUserId.toString());
		boolean scrapped = cacheStore.isMember(CachePolicy.POST_SCRAPS_USERS.createKey(postId), viewerUserId.toString());

		long likeCount = getCountWithFallback(postId, CachePolicy.POST_LIKES_COUNT, () -> likesRepository.countByPostId(postId));
		long scrapCount = getCountWithFallback(postId, CachePolicy.POST_SCRAPS_COUNT, () -> scrapRepository.countByPostId(postId));

		if (!liked) liked = likesRepository.existsByPostIdAndUserId(postId, viewerUserId);
		if (!scrapped) scrapped = scrapRepository.existsByPostIdAndUserId(postId, viewerUserId);

		PostInteractionState state = new PostInteractionState(liked, likeCount, scrapped, scrapCount);
		cacheStore.putInteractionCache(postId, viewerUserId, state);
		return state;
	}

	public Map<Long, PostInteractionCounts> getCounts(Collection<Long> postIds) {
		Map<Long, Long> likeMap = loadCounters(postIds, CachePolicy.POST_LIKES_COUNT, likesRepository::countByPostIds);
		Map<Long, Long> scrapMap = loadCounters(postIds, CachePolicy.POST_SCRAPS_COUNT, scrapRepository::countByPostIds);

		Map<Long, PostInteractionCounts> result = new LinkedHashMap<>(postIds.size());
		for (Long id : postIds) {
			result.put(id, new PostInteractionCounts(
				likeMap.getOrDefault(id, 0L),
				scrapMap.getOrDefault(id, 0L)
			));
		}
		return result;
	}

	public List<Long> getTopPostIdsRange(long start, long end) {
		return cacheStore.getTopPostIdsRange(start, end);
	}

	private long getCountWithFallback(Long postId, CachePolicy policy, LongSupplier dbFallback) {
		long cached = cacheStore.getCount(policy.createKey(postId));
		return cached != 0 ? cached : dbFallback.getAsLong();
	}

	private Map<Long, Long> loadCounters(Collection<Long> postIds, CachePolicy policy, Function<Collection<Long>, Map<Long, Long>> dbCounter) {
		Map<Long, Long> fromCache = cacheStore.getCountsBulk(postIds, policy);
		Set<Long> miss = fromCache.entrySet().stream()
			.filter(e -> e.getValue() == null)
			.map(Map.Entry::getKey)
			.collect(Collectors.toSet());
		if (!miss.isEmpty()) {
			Map<Long, Long> fromDb = dbCounter.apply(miss);
			fromCache.putAll(fromDb);
			cacheStore.putCountsBulk(fromDb, policy);
		}
		return fromCache;
	}
}