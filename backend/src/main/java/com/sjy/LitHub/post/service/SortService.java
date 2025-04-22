package com.sjy.LitHub.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.cache.post.PostListCacheManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SortService {

	private final PostRepository postRepository;
	private final PostListCacheManager postListCacheManager;
	private final PostMapper postMapper;

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> findDailyPopularPosts(Pageable pageable) {
		Page<PostSummaryResponseDTO> page = postListCacheManager.getOrPut(
			CachePolicy.POPULAR_POST,
			() -> postRepository.findPopularPosts(pageable),
			pageable.getPageNumber()
		);

		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> getMyPosts(Pageable pageable) {
		Page<PostSummaryResponseDTO> page = postRepository.findMyPosts(AuthUser.getUserId(), pageable);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> getScrappedPosts(Pageable pageable) {
		Page<PostSummaryResponseDTO> page = postRepository.findPostsScrappedByUser(AuthUser.getUserId(), pageable);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}
}