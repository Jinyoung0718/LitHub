package com.sjy.LitHub.post.cache;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.PostUpdatePart;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostDetailCacheUtils {

	private final PostCacheManager postCacheManager;
	private final PerCacheManager perCacheManager;

	public PostDetailResponseDTO getPostDetailWithPer(Long postId, Long userId, Supplier<PostDetailResponseDTO> dbFetcher) {
		String key = CachePolicy.POST_DETAIL.createKey(postId, userId);
		return perCacheManager.fetch(key, dbFetcher, PostDetailResponseDTO.class);
	} // PER 알고리즘 기반 게시글 상세 조회 캐싱

	public void updatePostDetailField(Long postId, Long userId, PostUpdatePart part, Object data) {
		String key = CachePolicy.POST_DETAIL.createKey(postId, userId);
		postCacheManager.updatePostDetailField(key, part, data);
	} // 게시글 상세 캐시 데이터 중 일부 필드만 수정 (예: 댓글 추가, 좋아요 토글 등)

	public void deletePostDetail(Long postId, Long userId) {
		String key = CachePolicy.POST_DETAIL.createKey(postId, userId);
		postCacheManager.deletePostDetail(key);
	} // 게시글 상세 캐시 무효화 (삭제)

	public void refreshPostDetail(Long postId, Long userId, Supplier<PostDetailResponseDTO> dbFetcher) {
		String key = CachePolicy.POST_DETAIL.createKey(postId, userId);
		postCacheManager.deletePostDetail(key);
		PostDetailResponseDTO updated = dbFetcher.get();
		postCacheManager.savePostDetail(key, updated);
	} // 게시글 상세 캐시 갱신
}