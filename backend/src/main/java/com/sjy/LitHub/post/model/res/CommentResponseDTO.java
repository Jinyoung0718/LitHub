package com.sjy.LitHub.post.model.res;

import java.time.LocalDateTime;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.post.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDTO {
	private Long id;
	private String content;
	private int depth;
	private LocalDateTime createdAt;

	private Long userId;
	private String userNickname;
	private String userProfileImageUrl;

	public static CommentResponseDTO from(Comment comment) {
		User user = comment.getUser();

		return CommentResponseDTO.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.depth(comment.getDepth())
			.createdAt(comment.getCreatedAt())
			.userId(user.getId())
			.userNickname(user.getDisplayNickname())
			.userProfileImageUrl(user.getProfileImageUrl256())
			.build();
	}
}