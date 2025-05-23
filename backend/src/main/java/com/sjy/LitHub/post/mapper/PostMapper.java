package com.sjy.LitHub.post.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.repository.user.UserGenFileRepository;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.post.cache.post.PostInteractionRedisManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.cache.enums.InteractionType;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.LikesRepository;
import com.sjy.LitHub.post.repository.ScrapRepository;
import com.sjy.LitHub.post.service.TagService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PostMapper {

	private final TagService tagService;
	private final PostInteractionRedisManager redisManager;
	private final LikesRepository likesRepository;
	private final ScrapRepository scrapRepository;
	private final PostGenFileRepository postGenFileRepository;
	private final UserGenFileRepository userGenFileRepository;
	private final RedisTemplate<String, String> redisTemplate;

	public PostMapper(
		TagService tagService,
		PostInteractionRedisManager redisManager,
		LikesRepository likesRepository,
		ScrapRepository scrapRepository,
		PostGenFileRepository postGenFileRepository,
		UserGenFileRepository userGenFileRepository,
		@Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate
	) {
		this.tagService = tagService;
		this.redisManager = redisManager;
		this.likesRepository = likesRepository;
		this.scrapRepository = scrapRepository;
		this.postGenFileRepository = postGenFileRepository;
		this.userGenFileRepository = userGenFileRepository;
		this.redisTemplate = redisTemplate;
	}

	public void enrichPostDetail(PostDetailResponseDTO dto, Long postId, Long userId) {
		dto.setTagNames(tagService.findTagNamesMap(List.of(postId)).getOrDefault(postId, List.of()));

		postGenFileRepository.findThumbnailByPostId(postId)
			.map(PostGenFile::getPublicUrl)
			.ifPresent(dto::setThumbnailImageUrl);

		userGenFileRepository.findProfile256ByUserId(userId)
			.map(UserGenFile::getPublicUrl)
			.ifPresent(dto::setProfileImageUrl);

		applyInteractionInfo(
			dto, postId, userId,
			PostDetailResponseDTO::setLiked,
			PostDetailResponseDTO::setScrapped,
			PostDetailResponseDTO::setLikeCount,
			PostDetailResponseDTO::setScrapCount
		);

		dto.setPopular(Boolean.TRUE.equals(
			redisTemplate.opsForSet().isMember(CachePolicy.POPULAR_POST_SET_KEY, postId.toString())
		));
	}

	public void enrichPostSummaries(List<PostSummaryResponseDTO> summaries) {
		if (summaries.isEmpty()) return;

		List<Long> postIds = summaries.stream().map(PostSummaryResponseDTO::getPostId).toList();
		Set<Long> userIds = summaries.stream().map(PostSummaryResponseDTO::getUserId).collect(Collectors.toSet());
		Long currentUserId = AuthUser.getUserId();

		Map<Long, List<String>> tagMap = tagService.findTagNamesMap(postIds);

		Map<Long, String> thumbnailMap = postGenFileRepository.findThumbnailsByPostIds(postIds).stream()
			.collect(Collectors.toMap(p -> p.getPost().getId(), PostGenFile::getPublicUrl));

		Map<Long, String> profileMap = userGenFileRepository.findProfiles256ByUserIds(userIds).stream()
			.collect(Collectors.toMap(p -> p.getUser().getId(), UserGenFile::getPublicUrl));

		List<CompletableFuture<Void>> futures = summaries.stream()
			.map(dto -> enrichSummaryAsync(dto, currentUserId, tagMap, thumbnailMap, profileMap))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	@Async("enrichExecutor")
	public CompletableFuture<Void> enrichSummaryAsync(
		PostSummaryResponseDTO dto,
		Long userId,
		Map<Long, List<String>> tagMap,
		Map<Long, String> thumbnailMap,
		Map<Long, String> profileMap
	) {
		Long postId = dto.getPostId();
		dto.setTagNames(tagMap.getOrDefault(postId, List.of()));
		dto.setThumbnailImageUrl(thumbnailMap.get(postId));
		dto.setProfileImageUrl(profileMap.get(dto.getUserId()));

		applyInteractionInfo(
			dto, postId, userId,
			PostSummaryResponseDTO::setLiked,
			PostSummaryResponseDTO::setScrapped,
			PostSummaryResponseDTO::setLikeCount,
			PostSummaryResponseDTO::setScrapCount
		);

		return CompletableFuture.completedFuture(null);
	}

	private <T> void applyInteractionInfo(
		T dto,
		Long postId,
		Long userId,
		BiConsumer<T, Boolean> likeSetter,
		BiConsumer<T, Boolean> scrapSetter,
		BiConsumer<T, Long> likeCountSetter,
		BiConsumer<T, Long> scrapCountSetter
	) {
		boolean liked = redisManager.hasInteraction(postId, userId, InteractionType.LIKE);
		boolean scrapped = redisManager.hasInteraction(postId, userId, InteractionType.SCRAP);
		long likeCount = redisManager.getInteractionCount(postId, InteractionType.LIKE);
		long scrapCount = redisManager.getInteractionCount(postId, InteractionType.SCRAP);

		if (likeCount == 0 && !liked) {
			liked = likesRepository.existsByPostIdAndUserId(postId, userId);
			likeCount = likesRepository.countByPostId(postId);
		}
		if (scrapCount == 0 && !scrapped) {
			scrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);
			scrapCount = scrapRepository.countByPostId(postId);
		}

		likeSetter.accept(dto, liked);
		scrapSetter.accept(dto, scrapped);
		likeCountSetter.accept(dto, likeCount);
		scrapCountSetter.accept(dto, scrapCount);
	}
}