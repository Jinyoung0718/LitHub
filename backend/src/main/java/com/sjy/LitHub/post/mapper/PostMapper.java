package com.sjy.LitHub.post.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.repository.user.UserGenFileRepository;
import com.sjy.LitHub.post.cache.interaction.InteractionStateManager;
import com.sjy.LitHub.post.cache.interaction.PostInteractionState;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.service.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMapper {

	private final TagService tagService;
	private final PostGenFileRepository postGenFileRepository;
	private final UserGenFileRepository userGenFileRepository;
	private final InteractionStateManager interactionStateManager;

	public void enrichPostDetail(PostDetailResponseDTO dto, Long postId, Long userId) {
		dto.setTagNames(tagService.findTagNamesMap(List.of(postId)).getOrDefault(postId, List.of()));

		postGenFileRepository.findThumbnailByPostId(postId)
			.map(PostGenFile::getPublicUrl)
			.ifPresent(dto::setThumbnailImageUrl);

		userGenFileRepository.findProfile256ByUserId(userId)
			.map(UserGenFile::getPublicUrl)
			.ifPresent(dto::setProfileImageUrl);

		applyInteractionInfo(dto, postId, userId);
	}

	public void enrichPostSummaries(List<PostSummaryResponseDTO> summaries) {
		if (summaries.isEmpty()) return;

		List<Long> postIds = summaries.stream()
			.map(PostSummaryResponseDTO::getPostId).toList();

		Set<Long> userIds = summaries.stream()
			.map(PostSummaryResponseDTO::getUserId)
			.collect(Collectors.toSet());

		Map<Long, List<String>> tagMap = tagService.findTagNamesMap(postIds);

		Map<Long, String> thumbnailMap = postGenFileRepository.findThumbnailsByPostIds(postIds).stream()
			.collect(Collectors.toMap(p -> p.getPost().getId(), PostGenFile::getPublicUrl));

		Map<Long, String> profileMap = userGenFileRepository.findProfiles256ByUserIds(userIds).stream()
			.collect(Collectors.toMap(p -> p.getUser().getId(), UserGenFile::getPublicUrl));

		for (PostSummaryResponseDTO dto : summaries) {
			Long postId = dto.getPostId();
			Long userId = dto.getUserId();

			dto.setTagNames(tagMap.getOrDefault(postId, List.of()));
			dto.setThumbnailImageUrl(thumbnailMap.get(postId));
			dto.setProfileImageUrl(profileMap.get(userId));

			PostInteractionState state = interactionStateManager.resolve(postId, userId);
			dto.setLikeCount(state.likeCount());
			dto.setScrapCount(state.scrapCount());
		}
	}

	public void applyInteractionInfo(PostDetailResponseDTO dto, Long postId, Long userId) {
		PostInteractionState state = interactionStateManager.resolve(postId, userId);
		dto.setLiked(state.liked());
		dto.setScrapped(state.scrapped());
		dto.setLikeCount(state.likeCount());
		dto.setScrapCount(state.scrapCount());
	}
}