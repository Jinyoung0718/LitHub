package com.sjy.LitHub.post.model.res.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class CommentTreeDTO {

	private Long id;
	private String content;
	private int depth;
	private LocalDateTime createdAt;

	private Long userId;
	private String userNickname;
	private String userProfileImageUrl;

	@Builder.Default
	private List<CommentTreeDTO> children = new ArrayList<>();

	public static CommentTreeDTO from(Comment comment) {
		User user = comment.getUser();

		return CommentTreeDTO.builder()
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