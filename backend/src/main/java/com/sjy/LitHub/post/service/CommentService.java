package com.sjy.LitHub.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.entity.Comment;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.mapper.CommentMapper;
import com.sjy.LitHub.post.model.req.CommentCreateRequestDTO;
import com.sjy.LitHub.post.model.res.comment.CommentResponseDTO;
import com.sjy.LitHub.post.model.res.comment.ReplyCommentDTO;
import com.sjy.LitHub.post.model.res.comment.RootCommentDTO;
import com.sjy.LitHub.post.repository.comment.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;

	@Transactional(readOnly = true)
	public PageResponse<RootCommentDTO> getRootComments(Long postId, Pageable pageable) {
		Page<Comment> rootComments = commentRepository.findByPostIdAndDepthOrderByCreatedAtAsc(postId, 0, pageable);

		List<RootCommentDTO> dtoList = rootComments.stream()
			.map(c -> RootCommentDTO.from(c, commentRepository.countByParentId(c.getId())))
			.toList();

		return PageResponse.from(new PageImpl<>(dtoList, pageable, rootComments.getTotalElements()));
	}

	@Transactional(readOnly = true)
	public PageResponse<ReplyCommentDTO> getRepliesComments(Long parentId, Pageable pageable) {
		Page<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(parentId, pageable);

		List<ReplyCommentDTO> dtoList = replies.stream()
			.map(ReplyCommentDTO::from)
			.toList();

		return PageResponse.from(new PageImpl<>(dtoList, pageable, replies.getTotalElements()));
	}

	@Transactional
	public CommentResponseDTO createComment(Long postId, CommentCreateRequestDTO dto) {
		User user = AuthUser.getAuthUser();
		Post post = Post.builder().id(postId).build();

		Comment parent = Optional.ofNullable(dto.getParentId())
			.flatMap(commentRepository::findById)
			.filter(c -> c.getDepth() < 1)
			.orElse(null);

		Comment comment = commentMapper.toEntity(user, post, dto, parent);
		commentRepository.save(comment);
		return CommentResponseDTO.from(comment);
	}

	@Transactional
	public CommentResponseDTO updateComment(Long commentId, CommentCreateRequestDTO dto) {
		User user = AuthUser.getAuthUser();
		Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId())
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.NO_AUTHORITY));

		comment.setContent(dto.getContent());
		return CommentResponseDTO.from(comment);
	}

	@Transactional
	public void deleteComment(Long commentId) {
		User user = AuthUser.getAuthUser();
		int deleted = commentRepository.deleteByIdAndUserId(commentId, user.getId());
		if (deleted == 0) {
			throw new InvalidUserException(BaseResponseStatus.NO_AUTHORITY);
		}
	}
}