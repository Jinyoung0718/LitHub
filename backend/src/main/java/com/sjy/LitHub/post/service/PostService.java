package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.service.post.MarkdownFileService;
import com.sjy.LitHub.file.service.post.ThumbnailPostService;
import com.sjy.LitHub.global.exception.custom.InvalidPostException;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.message.model.FanOutMessage;
import com.sjy.LitHub.global.message.model.PostDeletedEvent;
import com.sjy.LitHub.global.message.producer.FanOutProducer;
import com.sjy.LitHub.global.message.utils.InfluencerPolicy;
import com.sjy.LitHub.global.message.utils.RabbitMQConstants;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.post.cache.postdetail.PostDetailCacheService;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.entity.PostTag;
import com.sjy.LitHub.post.entity.Tag;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.req.PostContentUpdateDTO;
import com.sjy.LitHub.post.model.req.PostCreateRequestDTO;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final InfluencerPolicy influencerPolicy;
	private final RabbitTemplate rabbitTemplate;
	private final PostDetailCacheService postDetailCacheService;
	private final ThumbnailPostService thumbnailPostService;
	private final MarkdownFileService markdownFileService;
	private final FanOutProducer fanOutProducer;
	private final PostMapper postMapper;
	private final TagService tagService;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	@Transactional(readOnly = true)
	public PostDetailResponseDTO getPostDetail(Long postId) {
		return postDetailCacheService.getPostDetail(postId, () -> {
			PostDetailResponseDTO dto = fetchPostDetail(postId);
			if (dto == null) {
				throw new InvalidPostException(BaseResponseStatus.POST_NOT_FOUND);
			}
			return dto;
		});
	}

	private PostDetailResponseDTO fetchPostDetail(Long postId) {
		Long userId = AuthUser.getUserId();
		PostDetailResponseDTO dto = postRepository.findDetailDtoById(postId, userId)
			.orElseThrow(() -> new InvalidPostException(BaseResponseStatus.POST_NOT_FOUND));

		postMapper.enrichPostDetail(dto, postId, userId);
		return dto;
	}

	@Transactional
	public Long createPost(PostCreateRequestDTO request) {
		User user = AuthUser.getAuthUser();
		Post post = Post.from(request.getTitle(), request.getContentMarkdown(), user);
		thumbnailPostService.assignThumbnailToPost(post, request.getThumbnailFileName(), user.getId());

		List<Tag> tags = tagService.findOrCreateTags(request.getTags());
		tags.forEach(tag -> post.addPostTag(PostTag.of(post, tag)));
		postRepository.save(post);

		afterCommitExecutor.executeAfterCommit(() -> {
			if (!influencerPolicy.isInfluencer(user.getId())) {
				fanOutProducer.sendMessage(FanOutMessage.from(post));
			}
			postDetailCacheService.refreshPostDetail(
				post.getId(),
				() -> fetchPostDetail(post.getId())
			);
		});
		return post.getId();
	}

	@Transactional
	public void updatePostThumbnail(Long postId, MultipartFile thumbnail) {
		Long userId = AuthUser.getUserId();
		Post post = getOwnedPost(postId, userId);
		thumbnailPostService.updateThumbnail(post, thumbnail);

		afterCommitExecutor.executeAfterCommit(() ->
			postDetailCacheService.refreshPostDetail(postId, () -> fetchPostDetail(postId))
		);
	}

	@Transactional
	public void updatePostContent(Long postId, PostContentUpdateDTO dto) {
		Long userId = AuthUser.getUserId();
		Post post = getOwnedPost(postId, userId);

		if (dto.getContentMarkdown() != null) {
			post.updateContent(dto.getTitle(), dto.getContentMarkdown());
			markdownFileService.syncMarkdownImages(post, dto.getContentMarkdown());
		} else {
			post.updateTitle(dto.getTitle());
		}

		afterCommitExecutor.executeAfterCommit(() ->
			postDetailCacheService.refreshPostDetail(postId, () -> fetchPostDetail(postId))
		);
	}

	@Transactional
	public void deletePost(Long postId) {
		Long authorId = AuthUser.getUserId();
		int updated = postRepository.deletePost(postId, authorId);
		if (updated == 0) {
			throw new InvalidUserException(BaseResponseStatus.ACCESS_DENIED);
		}

		afterCommitExecutor.executeAfterCommit(() ->
			postDetailCacheService.deletePostDetail(postId)
		);

		rabbitTemplate.convertAndSend(
			RabbitMQConstants.POST_DELETED_EXCHANGE,
			RabbitMQConstants.POST_DELETED_ROUTING_KEY,
			new PostDeletedEvent(postId, authorId)
		);
	}

	private Post getOwnedPost(Long postId, Long userId) {
		return postRepository.findByIdAndUserId(postId, userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.NO_AUTHORITY));
	}
}