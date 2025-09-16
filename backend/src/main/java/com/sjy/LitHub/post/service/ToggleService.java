package com.sjy.LitHub.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.message.model.LikeToggledEvent;
import com.sjy.LitHub.global.message.model.ScrapToggledEvent;
import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.post.cache.interaction.InteractionEventPublisher;
import com.sjy.LitHub.post.entity.Likes;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.entity.Scrap;
import com.sjy.LitHub.post.model.res.toggle.LikeResponseDTO;
import com.sjy.LitHub.post.model.res.toggle.ScrapResponseDTO;
import com.sjy.LitHub.post.repository.like.LikesRepository;
import com.sjy.LitHub.post.repository.post.PostRepository;
import com.sjy.LitHub.post.repository.scrap.ScrapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToggleService {

	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final InteractionEventPublisher eventPublisher;

	@Transactional
	public LikeResponseDTO toggleLikes(Long postId) {
		Long userId = AuthUser.getUserId();
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);

		boolean alreadyLiked = likesRepository.existsByPostIdAndUserId(postId, userId);

		boolean nowLiked;
		if (alreadyLiked) {
			likesRepository.deleteByPostIdAndUserId(postId, userId);
			nowLiked = false;
		} else {
			likesRepository.save(Likes.of(post, user));
			nowLiked = true;
		}

		// MQ 발행
		eventPublisher.publishLikeToggled(new LikeToggledEvent(postId, userId, nowLiked));
		long newCount = likesRepository.countByPostId(postId);
		return new LikeResponseDTO(nowLiked, newCount);
	}

	@Transactional
	public ScrapResponseDTO toggleScrap(Long postId) {
		Long userId = AuthUser.getUserId();
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);

		boolean alreadyScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);

		boolean nowScrapped;
		if (alreadyScrapped) {
			scrapRepository.deleteByPostIdAndUserId(postId, userId);
			nowScrapped = false;
		} else {
			scrapRepository.save(Scrap.of(post, user));
			nowScrapped = true;
		}

		// MQ 발행
		eventPublisher.publishScrapToggled(new ScrapToggledEvent(postId, userId, nowScrapped));
		long newCount = scrapRepository.countByPostId(postId);
		return new ScrapResponseDTO(nowScrapped, newCount);
	}
}