package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.service.post.MarkdownImageService;
import com.sjy.LitHub.file.service.post.ThumbnailImageService;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.exception.custom.InvalidPostException;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.cache.PostDetailCacheUtils;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.entity.PostTag;
import com.sjy.LitHub.post.entity.Tag;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.req.PostCreateRequestDTO;
import com.sjy.LitHub.post.model.req.PostContentUpdateDTO;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final PostDetailCacheUtils postDetailCacheUtils;
	private final ThumbnailImageService thumbnailImageService;
	private final MarkdownImageService markdownImageService;
	private final PostMapper postMapper;
	private final TagService tagService;

	@Transactional(readOnly = true)
	public PostDetailResponseDTO getPostDetail(Long postId, boolean isPopular) {
		Long userId = AuthUser.getUserId();
		return isPopular
			? postDetailCacheUtils.getPostDetailWithPer(postId, userId, () -> fetchPostDetail(postId, userId))
			: postDetailCacheUtils.getNonPopularPostDetail(postId, userId, () -> fetchPostDetail(postId, userId));
	}

	private PostDetailResponseDTO fetchPostDetail(Long postId, Long userId) {
		PostDetailResponseDTO dto = postRepository.findDetailDtoById(postId, userId)
			.orElseThrow(() -> new InvalidPostException(BaseResponseStatus.POST_NOT_FOUND));

		postMapper.enrichPostDetail(dto, postId, userId);
		return dto;
	}

	@Transactional
	public Long createPost(PostCreateRequestDTO request) {
		User user = AuthUser.getAuthUser();
		Post post = Post.from(request.getTitle(), request.getContentMarkdown(), user);

		postRepository.save(post);
		thumbnailImageService.assignThumbnailToPost(post, request.getThumbnailFileName(), user.getId());

		List<Tag> tags = tagService.findOrCreateTags(request.getTags());
		tags.forEach(tag -> post.addPostTag(PostTag.of(post, tag)));

		postRepository.save(post);
		return post.getId();
	}

	@Transactional
	public void updatePostThumbnail(Long postId, MultipartFile thumbnail, boolean isPopular) {
		if (!isValidThumbnail(thumbnail)) {
			throw new InvalidFileException(BaseResponseStatus.THUMBNAIL_NOT_FOUND);
		}

		Long userId = AuthUser.getUserId();
		Post post = getOwnedPost(postId, userId);
		thumbnailImageService.updateThumbnail(post, thumbnail);
		postDetailCacheUtils.refreshPostDetail(postId, userId, isPopular, () -> fetchPostDetail(postId, userId));
	}

	@Transactional
	public void updatePostContent(Long postId, PostContentUpdateDTO request, boolean isPopular) {
		Long userId = AuthUser.getUserId();
		Post post = getOwnedPost(postId, userId);
		post.updateContent(request.getTitle(), request.getContentMarkdown());

		markdownImageService.syncMarkdownImages(post, request.getContentMarkdown());
		postRepository.flush();
		postDetailCacheUtils.refreshPostDetail(postId, userId, isPopular, () -> fetchPostDetail(postId, userId));
	}

	private Post getOwnedPost(Long postId, Long userId) {
		return postRepository.findByIdAndUserId(postId, userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.NO_AUTHORITY));
	}

	private boolean isValidThumbnail(MultipartFile thumbnail) {
		return thumbnail != null && !thumbnail.isEmpty();
	}

	@Transactional
	public void deletePost(Long postId, boolean isPopular) {
		Long userId = AuthUser.getUserId();
		int deleted = postRepository.deletePost(postId, userId);
		if (deleted == 0) {
			throw new InvalidUserException(BaseResponseStatus.ACCESS_DENIED);
		}

		postDetailCacheUtils.deletePostDetail(postId, userId, isPopular);
	}
}