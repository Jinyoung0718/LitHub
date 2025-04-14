package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.repository.friend.FriendRepository;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.post.cache.PostListCacheManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.model.res.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;
import com.sjy.LitHub.post.cache.keyword.PopularKeywordManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SortService {

	private final FriendRepository friendRepository;
	private final PostRepository postRepository;
	private final PostListCacheManager postListCacheManager;
	private final PopularKeywordManager popularKeywordManager;

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> searchByKeyword(String keyword, Pageable pageable) {
		popularKeywordManager.onSearch(keyword); // 검색어 ZSet 기록

		Pageable limited = PageRequest.of(pageable.getPageNumber(), 15);
		return postListCacheManager.getOrPut(CachePolicy.SEARCH_POST,
			() -> postRepository.searchByKeyword(keyword, limited),
			keyword, pageable.getPageNumber()); // 캐시가 있으면 조회, 없으면 조회 후 저장
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> searchByTag(String tagName, Pageable pageable) {
		popularKeywordManager.onTagSearch(tagName); // 검색어 ZSet 기록

		Pageable limited = PageRequest.of(pageable.getPageNumber(), 15);
		return postListCacheManager.getOrPut(CachePolicy.TAG_POST,
			() -> postRepository.searchByTag(tagName, limited),
			tagName, pageable.getPageNumber());
	}

	@Transactional(readOnly = true)
	public Page<PostSummaryResponseDTO> findDailyPopularPosts(Pageable pageable) {
		return postListCacheManager.getOrPut(CachePolicy.POPULAR_POST,
			() -> postRepository.findPopularPosts(pageable),
			pageable.getPageNumber());
	}

	@Transactional(readOnly = true)
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