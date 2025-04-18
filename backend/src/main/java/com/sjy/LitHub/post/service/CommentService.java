package com.sjy.LitHub.post.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.entity.Comment;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.mapper.CommentMapper;
import com.sjy.LitHub.post.model.req.CommentCreateRequestDTO;
import com.sjy.LitHub.post.model.res.comment.CommentResponseDTO;
import com.sjy.LitHub.post.model.res.comment.CommentTreeDTO;
import com.sjy.LitHub.post.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;

	@Transactional(readOnly = true)
	public List<CommentTreeDTO> getCommentTree(Long postId) {
		List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
		Map<Long, CommentTreeDTO> dtoMap = new LinkedHashMap<>();
		List<CommentTreeDTO> roots = new ArrayList<>();

		for (Comment comment : comments) {
			CommentTreeDTO dto = CommentTreeDTO.from(comment);
			dtoMap.put(dto.getId(), dto);
			if (comment.getParent() == null || comment.getDepth() == 0) {
				roots.add(dto);
			} else {
				Long parentId = comment.getParent().getId();
				dtoMap.get(parentId).getChildren().add(dto);
			}
		}
		return roots;
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