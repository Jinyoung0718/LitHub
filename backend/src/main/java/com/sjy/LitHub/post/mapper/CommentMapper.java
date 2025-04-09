package com.sjy.LitHub.post.mapper;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.post.entity.Comment;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.model.req.CommentCreateRequestDTO;

@Component
public class CommentMapper {

	public Comment toEntity(User user, Post post, CommentCreateRequestDTO dto, Comment parent) {
		return Comment.builder()
			.user(user)
			.post(post)
			.content(dto.getContent())
			.parent(parent)
			.depth(parent == null ? 0 : parent.getDepth() + 1)
			.build();
	}
}