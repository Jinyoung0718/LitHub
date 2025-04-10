package com.sjy.LitHub.post.model.res;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSummaryResponseDTO {
	private Long postId;
	private String title;
	private String thumbnailUrl;

	private String authorNickname;
	private String authorProfileImageUrl;

	private int likeCount;
	private int scrapCount;
	private int commentCount;

	private boolean liked;
	private boolean scrapped;

	private LocalDateTime createdAt;
	private List<String> tagNames;
}