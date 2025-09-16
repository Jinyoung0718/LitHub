package com.sjy.LitHub.post.model.res.comment;

import java.time.LocalDateTime;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.post.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCommentDTO {

	private Long id;

	private String content;

	private LocalDateTime createdAt;

	private Long userId;

	private String userNickname;

	private String userProfileImageUrl;

	public static ReplyCommentDTO from(Comment comment) {
		User user = comment.getUser();
		return ReplyCommentDTO.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.userId(user.getId())
			.userNickname(user.getDisplayNickname())
			.userProfileImageUrl(user.getProfileImageUrl256())
			.build();
	}
}