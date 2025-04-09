package com.sjy.LitHub.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public LikeResponseDTO toggleLikes(Long userId, Long postId) {
		boolean isLiked = likesRepository.existsByPostIdAndUserId(postId, userId);

		if (isLiked) {
			likesRepository.toggleLikeIfExists(postId, userId);
		} else {
			likesRepository.save(toggleMapper.toLikes(userId, postId));
		}

		return new LikeResponseDTO(!isLiked, likesRepository.countByPostId(postId));
	}

	@Transactional
	public ScrapResponseDTO toggleScrap(Long userId, Long postId) {
		boolean isScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);

		if (isScrapped) {
			scrapRepository.toggleScrapIfExists(postId, userId);
		} else {
			Post post = postRepository.getReferenceById(postId);
			scrapRepository.save(toggleMapper.toScrap(userId, post));
		}

		return new ScrapResponseDTO(!isScrapped, scrapRepository.countByPostId(postId));
	}
}