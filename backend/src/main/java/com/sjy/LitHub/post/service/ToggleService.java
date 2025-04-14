package com.sjy.LitHub.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.post.cache.PostDetailCacheUtils;
import com.sjy.LitHub.post.cache.enums.PostUpdatePart;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.mapper.ToggleMapper;
import com.sjy.LitHub.post.model.res.LikeResponseDTO;
import com.sjy.LitHub.post.model.res.ScrapResponseDTO;
import com.sjy.LitHub.post.repository.LikesRepository;
import com.sjy.LitHub.post.repository.ScrapRepository;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToggleService {

	private final LikesRepository likesRepository;
	private final PostRepository postRepository;
	private final ScrapRepository scrapRepository;
	private final ToggleMapper toggleMapper;
	private final PostDetailCacheUtils postDetailCacheUtils;

	@Transactional
	public LikeResponseDTO toggleLikes(Long postId, boolean isPopular) {
		Long userId = AuthUser.getUserId();
		boolean isLiked = likesRepository.existsByPostIdAndUserId(postId, userId);

		if (isLiked) {
			likesRepository.toggleLikeIfExists(postId, userId);
		} else {
			likesRepository.save(toggleMapper.toLikes(userId, postId));
		}

		if (isPopular) {
			postDetailCacheUtils.updatePostDetailField(postId, userId, PostUpdatePart.TOGGLE_LIKE, null);
		}

		return new LikeResponseDTO(!isLiked, likesRepository.countByPostId(postId));
	}

	@Transactional
	public ScrapResponseDTO toggleScrap(Long postId, boolean isPopular) {
		Long userId = AuthUser.getUserId();
		boolean isScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);

		if (isScrapped) {
			scrapRepository.toggleScrapIfExists(postId, userId);
		} else {
			Post post = postRepository.getReferenceById(postId);
			scrapRepository.save(toggleMapper.toScrap(userId, post));
		}

		if (isPopular) {
			postDetailCacheUtils.updatePostDetailField(postId, userId, PostUpdatePart.TOGGLE_SCRAP, null);
		}

		return new ScrapResponseDTO(!isScrapped, scrapRepository.countByPostId(postId));
	}
}