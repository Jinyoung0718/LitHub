package com.sjy.LitHub.post.cache.postdetail;

import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.postlist.PopularPostRefresher;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PostDetailCacheService {

	private final RedisTemplate<String, String> redisTemplate;
	private final PostDetailCacheStore postDetailCacheStore;
	private final PopularPostRefresher popularPostRefresher;

	public PostDetailCacheService(
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
		PostDetailCacheStore postDetailCacheStore,
		PopularPostRefresher popularPostRefresher
	) {
		this.redisTemplate = redisTemplate;
		this.postDetailCacheStore = postDetailCacheStore;
		this.popularPostRefresher = popularPostRefresher;
	}

	public PostDetailResponseDTO getPostDetail(Long postId, Supplier<PostDetailResponseDTO> dbFetcher) {
		CachePolicy policy = resolvePolicy(postId);
		String key = policy.createKey(postId);

		PostDetailResponseDTO cached = postDetailCacheStore.get(key);
		if (cached != null) {
			popularPostRefresher.refreshOnAccess(postId); // 인기 점수 차감 (0.1)
			return cached;
		}

		PostDetailResponseDTO result = dbFetcher.get();
		postDetailCacheStore.put(key, result, policy.getTtl());
		return result;
	}

	public void refreshPostDetail(Long postId, Supplier<PostDetailResponseDTO> dbFetcher) {
		CachePolicy policy = resolvePolicy(postId);
		String key = policy.createKey(postId);
		PostDetailResponseDTO dto = dbFetcher.get();
		postDetailCacheStore.put(key, dto, policy.getTtl());
		log.info("[PostDetail 캐시 갱신 완료] postId: {}", postId);
	} // 게시글 갱신 시, 캐싱 리프레시 Write-Through

	public void deletePostDetail(Long postId) {
		// 상세 캐시 삭제
		CachePolicy policy = resolvePolicy(postId);
		String key = policy.createKey(postId);
		postDetailCacheStore.delete(key);

		// 검색 캐시 무효화
		invalidateRelatedSearchCaches(postId);

		// 인기글 ZSET에서 제거
		String popularKey = CachePolicy.POPULAR_POST_ZSET.getKeyFormat();
		redisTemplate.opsForZSet().remove(popularKey, postId.toString());

		// 인터랙션 캐시 삭제
		redisTemplate.delete(CachePolicy.POST_LIKES_COUNT.createKey(postId));
		redisTemplate.delete(CachePolicy.POST_SCRAPS_COUNT.createKey(postId));
		redisTemplate.delete(CachePolicy.POST_LIKES_USERS.createKey(postId));
		redisTemplate.delete(CachePolicy.POST_SCRAPS_USERS.createKey(postId));
	}

	public void invalidateRelatedSearchCaches(Long postId) {
		String relatedKey = CachePolicy.RELATED_SEARCH_CACHE_KEYS.createKey(postId);
		Set<String> keys = redisTemplate.opsForSet().members(relatedKey);
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);   // 관련 검색 캐시 삭제
		}
		redisTemplate.delete(relatedKey); // 역색인 키 자체도 삭제
	}

	private boolean isPopular(Long postId) {
		Double score = redisTemplate.opsForZSet()
			.score(CachePolicy.POPULAR_POST_ZSET.getKeyFormat(), postId.toString());
		return score != null;
	}

	private CachePolicy resolvePolicy(Long postId) {
		return isPopular(postId)
			? CachePolicy.POST_DETAIL_POPULAR
			: CachePolicy.POST_DETAIL_NON_POPULAR;
	}
}