package com.sjy.LitHub.post.model.res.post;

import java.time.LocalDateTime;
import java.util.List;

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
public class PostSummaryResponseDTO {
	private Long postId;
	private String title;
	private Long userId;
	private String authorNickname;

	private Long likeCount;
	private Long scrapCount;
	private Long commentCount;
	private LocalDateTime createdAt;

	private String thumbnailImageUrl;
	private String profileImageUrl;

	private boolean liked;
	private boolean scrapped;

	private List<String> tagNames;

	@SuppressWarnings("unused")
	public PostSummaryResponseDTO(
		Long postId,
		String title,
		Long userId,
		String authorNickname,
		Long likeCount,
		Long scrapCount,
		Long commentCount,
		LocalDateTime createdAt,
		String thumbnailImageUrl,
		String profileImageUrl
	) {
		this.postId = postId;
		this.title = title;
		this.userId = userId;
		this.authorNickname = authorNickname;
		this.likeCount = likeCount;
		this.scrapCount = scrapCount;
		this.commentCount = commentCount;
		this.createdAt = createdAt;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.profileImageUrl = profileImageUrl;
		this.tagNames = List.of();
	}
}