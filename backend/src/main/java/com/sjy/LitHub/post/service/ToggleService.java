package com.sjy.LitHub.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.post.cache.post.PostInteractionRedisManager;
import com.sjy.LitHub.post.cache.enums.InteractionType;
import com.sjy.LitHub.post.model.res.toggle.LikeResponseDTO;
import com.sjy.LitHub.post.model.res.toggle.ScrapResponseDTO;
import com.sjy.LitHub.post.repository.LikesRepository;
import com.sjy.LitHub.post.repository.ScrapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToggleService {

	private final PostInteractionRedisManager redisManager;
	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;

	@Transactional
	public LikeResponseDTO toggleLikes(Long postId) {
		Long userId = AuthUser.getUserId();
		redisManager.toggleInteraction(postId, userId, InteractionType.LIKE);

		boolean nowLiked = redisManager.hasInteraction(postId, userId, InteractionType.LIKE);
		long likeCount = redisManager.getInteractionCount(postId, InteractionType.LIKE);

		if (likeCount == 0 && !nowLiked) {
			nowLiked = likesRepository.existsByPostIdAndUserId(postId, userId);
			likeCount = likesRepository.countByPostId(postId);
		}

		return new LikeResponseDTO(nowLiked, likeCount);
	}

	@Transactional
	public ScrapResponseDTO toggleScrap(Long postId) {
		Long userId = AuthUser.getUserId();
		redisManager.toggleInteraction(postId, userId, InteractionType.SCRAP);

		boolean nowScrapped = redisManager.hasInteraction(postId, userId, InteractionType.SCRAP);
		long scrapCount = redisManager.getInteractionCount(postId, InteractionType.SCRAP);

		if (scrapCount == 0 && !nowScrapped) {
			nowScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);
			scrapCount = scrapRepository.countByPostId(postId);
		}

		return new ScrapResponseDTO(nowScrapped, scrapCount);
	}
}