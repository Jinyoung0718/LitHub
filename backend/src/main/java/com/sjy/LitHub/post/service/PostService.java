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
	private final PostImageService postImageService;
	private final TagService tagService;

	@Transactional(readOnly = true)
	public PostDetailResponseDTO getPostDetail(Long postId) {
		return postRepository.findDetailDtoById(postId, AuthUser.getUserId())
			.orElseThrow(() -> new InvalidPostException(BaseResponseStatus.POST_NOT_FOUND));
	}

	@Transactional
	public Long createPost(PostCreateRequestDTO request, MultipartFile thumbnail) {
		Post post = Post.from(request.getTitle(), request.getContentMarkdown(), AuthUser.getAuthUser());
		postImageService.assignImagesToPost(post, thumbnail, AuthUser.getUserId());

		List<Tag> tags = tagService.findOrCreateTags(request.getTags());
		for (Tag tag : tags) {
			post.addPostTag(PostTag.of(post, tag));
		}

		postRepository.save(post);
		return post.getId();
	}

	@Transactional
	public void updatePost(Long postId, PostUpdateRequestDTO request) {
		Post post = postRepository.findByIdAndUserId(postId, AuthUser.getUserId())
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.NO_AUTHORITY));

		post.updateContent(request.getTitle(), request.getContentMarkdown());
		if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
			postImageService.updateThumbnail(post, request.getThumbnail());
		}

		postImageService.syncMarkdownImages(post, request.getContentMarkdown());
	}

	@Transactional
	public void deletePost(Long postId) {
		int deletedCount = postRepository.deletePost(postId, AuthUser.getUserId());
		if (deletedCount == 0) {
			throw new InvalidUserException(BaseResponseStatus.ACCESS_DENIED);
		}
	}
}