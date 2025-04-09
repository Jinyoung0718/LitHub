package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.repository.friend.FriendRepository;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.post.model.res.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SortService {

	private final FriendRepository friendRepository;
	private final PostRepository postRepository;

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> search(String keyword, Pageable pageable) {
		return postRepository.searchPosts(keyword, pageable);
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> findPostsByTag(String tagName, Pageable pageable) {
		return postRepository.findPostsByTag(tagName, pageable);
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> findPopularPosts(Pageable pageable) {
		return postRepository.findPopularPosts(pageable);
	}

	public Page<PostSummaryResponseDTO> getLikedPosts(Pageable pageable) {
		return postRepository.findPostsLikedByUser(AuthUser.getUserId(), pageable);
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> getScrappedPosts(Pageable pageable) {
		return postRepository.findPostsScrappedByUser(AuthUser.getUserId(), pageable);
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> getFollowerFeed(Pageable pageable) {
		List<Long> followeeIds = friendRepository.findFriendIdsByUserId(AuthUser.getUserId());
		return postRepository.findFollowerFeedsByPriority(followeeIds, pageable);
	}
}