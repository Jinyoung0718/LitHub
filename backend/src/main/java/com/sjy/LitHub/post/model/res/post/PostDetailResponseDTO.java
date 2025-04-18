package com.sjy.LitHub.post.model.res.post;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDTO {

	private Long postId;
	private String title;
	private String contentMarkdown;

	private String authorNickname;

	private Long likeCount;
	private Long scrapCount;
	private Long commentCount;

	private boolean isPopular;
	private boolean liked;
	private boolean scrapped;
	private boolean editable;

	private LocalDate createdAt;

	private String thumbnailImageUrl;
	private String profileImageUrl;

	private List<String> tagNames;

	@SuppressWarnings("unused")
	public PostDetailResponseDTO(
		Long postId,
		String title,
		String contentMarkdown,
		String authorNickname,
		Long likeCount,
		Long scrapCount,
		Long commentCount,
		boolean isPopular,
		boolean liked,
		boolean scrapped,
		boolean editable,
		LocalDate createdAt,
		String thumbnailImageUrl,
		String profileImageUrl
	) {
		this.postId = postId;
		this.title = title;
		this.contentMarkdown = contentMarkdown;
		this.authorNickname = authorNickname;
		this.likeCount = likeCount;
		this.scrapCount = scrapCount;
		this.commentCount = commentCount;
		this.isPopular = isPopular;
		this.liked = liked;
		this.scrapped = scrapped;
		this.editable = editable;
		this.createdAt = createdAt;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.profileImageUrl = profileImageUrl;
		this.tagNames = List.of();
	}
}