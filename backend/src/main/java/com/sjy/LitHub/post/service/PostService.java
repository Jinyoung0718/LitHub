package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.service.PostImageService;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidPostException;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.cache.PostDetailCacheUtils;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.entity.PostTag;
import com.sjy.LitHub.post.entity.Tag;
import com.sjy.LitHub.post.model.req.PostCreateRequestDTO;
import com.sjy.LitHub.post.model.req.PostUpdateRequestDTO;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final PostDetailCacheUtils postDetailCacheUtils;
	private final PostImageService postImageService;
	private final TagService tagService;

	@Transactional(readOnly = true)
	public PostDetailResponseDTO getPostDetail(Long postId, boolean isPopular) {
		Long userId = AuthUser.getUserId();

		return isPopular
			? postDetailCacheUtils.getPostDetailWithPer(postId, userId, () -> fetchPostDetail(postId, userId))
			: fetchPostDetail(postId, userId);
	}

	@Transactional
	public Long createPost(PostCreateRequestDTO request, MultipartFile thumbnail) {
		Post post = Post.from(request.getTitle(), request.getContentMarkdown(), AuthUser.getAuthUser());
		postImageService.assignImagesToPost(post, thumbnail, AuthUser.getUserId());

		List<Tag> tags = tagService.findOrCreateTags(request.getTags());
		tags.forEach(tag -> post.addPostTag(PostTag.of(post, tag)));

		postRepository.save(post);
		return post.getId();
	}

	@Transactional
	public void updatePost(Long postId, PostUpdateRequestDTO request) {
		Long userId = AuthUser.getUserId();
		Post post = getOwnedPost(postId, userId);

		post.updateContent(request.getTitle(), request.getContentMarkdown());

		if (isValidThumbnail(request.getThumbnail())) {
			postImageService.updateThumbnail(post, request.getThumbnail());
		}

		postImageService.syncMarkdownImages(post, request.getContentMarkdown());
		postRepository.flush();
		postDetailCacheUtils.refreshPostDetail(postId, userId, () -> fetchPostDetail(postId, userId));
	}

	@Transactional
	public void deletePost(Long postId) {
		Long userId = AuthUser.getUserId();

		int deleted = postRepository.deletePost(postId, userId);
		if (deleted == 0) {
			throw new InvalidUserException(BaseResponseStatus.ACCESS_DENIED);
		}
		postDetailCacheUtils.deletePostDetail(postId, userId);
	}

	private Post getOwnedPost(Long postId, Long userId) {
		return postRepository.findByIdAndUserId(postId, userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.NO_AUTHORITY));
	}

	private boolean isValidThumbnail(MultipartFile thumbnail) {
		return thumbnail != null && !thumbnail.isEmpty();
	}

	private PostDetailResponseDTO fetchPostDetail(Long postId, Long userId) {
		return postRepository.findDetailDtoById(postId, userId)
			.orElseThrow(() -> new InvalidPostException(BaseResponseStatus.POST_NOT_FOUND));
	}
}