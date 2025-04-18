package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.repository.follow.FollowRepository;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.post.cache.PostListCacheManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.PopularKeywordManager;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SortService {

	private final FollowRepository followRepository;
	private final PostRepository postRepository;
	private final PostListCacheManager postListCacheManager;
	private final PopularKeywordManager popularKeywordManager;
	private final PostMapper postMapper;

	private static final int PAGE_SIZE = 15;

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> searchByKeyword(String keyword, Pageable pageable) {
		popularKeywordManager.onSearch(keyword);
		Pageable limited = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE);

		return postListCacheManager.getOrPut(CachePolicy.SEARCH_POST,
			() -> enrich(postRepository.searchByKeyword(keyword, limited)),
			keyword, pageable.getPageNumber());
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> searchByTag(String tagName, Pageable pageable) {
		popularKeywordManager.onTagSearch(tagName);
		Pageable limited = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE);

		return postListCacheManager.getOrPut(CachePolicy.TAG_POST,
			() -> enrich(postRepository.searchByTag(tagName, limited)),
			tagName, pageable.getPageNumber());
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> findDailyPopularPosts(Pageable pageable) {
		return postListCacheManager.getOrPut(CachePolicy.POPULAR_POST,
			() -> enrich(postRepository.findPopularPosts(pageable)),
			pageable.getPageNumber());
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> getMyPosts(Pageable pageable) {
		return enrich(postRepository.findMyPosts( AuthUser.getUserId(), pageable));
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> getScrappedPosts(Pageable pageable) {
		return enrich(postRepository.findPostsScrappedByUser(AuthUser.getUserId(), pageable));
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> getFollowerFeed(Pageable pageable) {
		List<Long> followeeIds = followRepository.findFolloweeIdsByUserId(AuthUser.getUserId());
		if (followeeIds.isEmpty()) return Page.empty(pageable);
		return enrich(postRepository.findFollowerFeedsByPriority(followeeIds, pageable));
	}

	private Page<PostSummaryResponseDTO> enrich(Page<PostSummaryResponseDTO> page) {
		postMapper.enrichPostSummaries(page.getContent());
		return page;
	}
}