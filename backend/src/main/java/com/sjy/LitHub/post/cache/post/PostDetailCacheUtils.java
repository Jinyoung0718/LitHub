package com.sjy.LitHub.post.cache.post;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostDetailCacheUtils {

	private final PostDetailCacheManager postDetailCacheManager;
	private final PerCacheManager perCacheManager;

	public PostDetailResponseDTO getPostDetailWithPer(Long postId, Long userId, Supplier<PostDetailResponseDTO> dbFetcher) {
		String key = CachePolicy.POST_DETAIL.createKey(postId, userId);
		return perCacheManager.fetch(key, dbFetcher, PostDetailResponseDTO.class);
	} // PER 알고리즘 기반 게시글 상세 조회 캐싱

	public PostDetailResponseDTO getNonPopularPostDetail(Long postId, Long userId, Supplier<PostDetailResponseDTO> dbFetcher) {
		String key = CachePolicy.POST_DETAIL_NON_POPULAR.createKey(postId, userId);
		return postDetailCacheManager.getOrPut(key, dbFetcher);
	}

	public void deletePostDetail(Long postId, Long userId, boolean isPopular) {
		String key = (isPopular ? CachePolicy.POST_DETAIL : CachePolicy.POST_DETAIL_NON_POPULAR).createKey(postId, userId);
		postDetailCacheManager.deletePostDetail(key);
	} // 게시글 상세 캐시 무효화 (삭제)

	public void refreshPostDetail(Long postId, Long userId, boolean isPopular, Supplier<PostDetailResponseDTO> dbFetcher) {
		String key = (isPopular ? CachePolicy.POST_DETAIL : CachePolicy.POST_DETAIL_NON_POPULAR).createKey(postId, userId);
		postDetailCacheManager.deletePostDetail(key);
		PostDetailResponseDTO updated = dbFetcher.get();
		postDetailCacheManager.savePostDetail(key, updated);
	} // 게시글 상세 캐시 갱신
}