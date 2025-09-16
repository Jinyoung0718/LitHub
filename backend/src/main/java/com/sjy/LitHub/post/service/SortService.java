package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.global.message.utils.FeedRedisUtil;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.cache.interaction.InteractionReadService;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SortService {

	private final PostMapper postMapper;
	private final PostRepository postRepository;
	private final InteractionReadService interactionReadService;
	private final FeedRedisUtil feedRedisUtil;

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> findPopularPosts(Pageable pageable) {
		long start = pageable.getOffset();
		long end = start + pageable.getPageSize() - 1;

		List<Long> pagePostIds = interactionReadService.getTopPostIdsRange(start, end);
		if (pagePostIds.isEmpty()) {
			return PageResponse.empty(pageable);
		}

		List<PostSummaryResponseDTO> rows = postRepository.findByIds(pagePostIds);
		postMapper.enrichPostSummaries(rows);
		return PageResponse.from(rows, pageable);
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

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> getFollowerFeed(Pageable pageable) {
		long start = pageable.getOffset();
		long end = start + (pageable.getPageSize() - 1);

		List<Long> pagePostIds = feedRedisUtil.getFeedPostIdsRange(AuthUser.getUserId(), start, end);
		if (pagePostIds.isEmpty()) {
			return PageResponse.empty(pageable);
		}

		List<PostSummaryResponseDTO> rows = postRepository.findByIds(pagePostIds);
		postMapper.enrichPostSummaries(rows);
		return PageResponse.from(rows, pageable);
	}
}