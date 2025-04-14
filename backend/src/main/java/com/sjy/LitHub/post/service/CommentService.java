package com.sjy.LitHub.post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.cache.PostDetailCacheUtils;
import com.sjy.LitHub.post.cache.enums.PostUpdatePart;
import com.sjy.LitHub.post.entity.Comment;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.mapper.CommentMapper;
import com.sjy.LitHub.post.model.req.CommentCreateRequestDTO;
import com.sjy.LitHub.post.model.res.CommentResponseDTO;
import com.sjy.LitHub.post.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;
	private final PostDetailCacheUtils postDetailCacheUtils;

	@Transactional
	public CommentResponseDTO createComment(Long postId, CommentCreateRequestDTO dto, boolean isPopular) {
		User user = AuthUser.getAuthUser();
		Post post = Post.builder().id(postId).build();

		Comment parent = Optional.ofNullable(dto.getParentId())
			.flatMap(commentRepository::findById)
			.filter(c -> c.getDepth() < 1)
			.orElse(null);

		Comment comment = commentMapper.toEntity(user, post, dto, parent);
		commentRepository.save(comment);

		CommentResponseDTO response = CommentResponseDTO.from(comment);
		if (isPopular) {
			postDetailCacheUtils.updatePostDetailField(postId, user.getId(), PostUpdatePart.ADD_COMMENT, response);
		}

		return response;
	}

	@Transactional
	public CommentResponseDTO updateComment(Long commentId, CommentCreateRequestDTO dto, boolean isPopular, Long postId) {
		User user = AuthUser.getAuthUser();
		Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId())
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.NO_AUTHORITY));

		comment.setContent(dto.getContent());

		CommentResponseDTO response = CommentResponseDTO.from(comment);
		if (isPopular) {
			postDetailCacheUtils.updatePostDetailField(postId, user.getId(), PostUpdatePart.EDIT_COMMENT, response);
		}
		return response;
	}

	@Transactional
	public void deleteComment(Long commentId, boolean isPopular, Long postId) {
		User user = AuthUser.getAuthUser();
		int deleted = commentRepository.deleteByIdAndUserId(commentId, user.getId());
		if (deleted == 0) {
			throw new InvalidUserException(BaseResponseStatus.NO_AUTHORITY);
		}

		if (isPopular) {
			postDetailCacheUtils.updatePostDetailField(postId, user.getId(), PostUpdatePart.REMOVE_COMMENT, commentId);
		}
	}
}