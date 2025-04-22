package com.sjy.LitHub.post.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.repository.follow.FollowRepository;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.message.FeedRedisUtil;
import com.sjy.LitHub.global.message.InfluencerPolicy;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {

	private final FollowRepository followRepository;
	private final PostRepository postRepository;
	private final InfluencerPolicy influencerPolicy;
	private final FeedRedisUtil feedRedisUtil;
	private final PostMapper postMapper;

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> getMergedFollowerFeed(Pageable pageable) {
		Long currentUserId = AuthUser.getUserId();
		List<Long> followeeIds = followRepository.findFolloweeIdsByUserId(currentUserId);
		if (followeeIds.isEmpty()) {
			return new PageResponse<>(List.of(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, true);
		}

		List<Long> influencers = followeeIds.stream()
			.filter(influencerPolicy::isInfluencer)
			.toList();

		List<PostSummaryResponseDTO> fanoutPosts = getFanoutPosts(currentUserId);

		List<PostSummaryResponseDTO> influencerPosts = getInfluencerPosts(influencers);

		List<PostSummaryResponseDTO> merged = mergeAndPaginate(fanoutPosts, influencerPosts, pageable);

		postMapper.enrichPostSummaries(merged);
		Page<PostSummaryResponseDTO> page = new PageImpl<>(merged, pageable, fanoutPosts.size() + influencerPosts.size());
		return PageResponse.from(page);
	}

	private List<PostSummaryResponseDTO> getFanoutPosts(Long userId) {
		List<Long> postIds = feedRedisUtil.getFeedPostIds(userId, 100);
		return postRepository.findByIds(postIds);
	}

	private List<PostSummaryResponseDTO> getInfluencerPosts(List<Long> influencerIds) {
		if (influencerIds == null || influencerIds.isEmpty()) return List.of();
		return postRepository.findRecentByUserIds(influencerIds, 100);
	}

	private List<PostSummaryResponseDTO> mergeAndPaginate(
		List<PostSummaryResponseDTO> list1,
		List<PostSummaryResponseDTO> list2,
		Pageable pageable) {
		return Stream.concat(list1.stream(), list2.stream())
			.sorted(Comparator.comparing(PostSummaryResponseDTO::getCreatedAt).reversed())
			.skip(pageable.getOffset())
			.limit(pageable.getPageSize())
			.toList();
	}
}