package com.sjy.LitHub.post.model.res;

import java.time.LocalDateTime;
import java.util.List;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponseDTO {
	private Long postId;
	private String title;
	private String contentMarkdown;
	private String thumbnailUrl;

	private String authorNickname;
	private String authorProfileImageUrl;

	private int likeCount;
	private int scrapCount;
	private int commentCount;

	private boolean liked;
	private boolean scrapped;
	private boolean editable;

	private LocalDateTime createdAt;
	private List<String> tagNames;

	private List<CommentResponseDTO> comments;

	public static PostDetailResponseDTO from(Post post, User currentUser, List<CommentResponseDTO> commentDTOs) {
		boolean liked = post.getLikes().stream()
			.anyMatch(like -> like.getUser().equals(currentUser));

		boolean scrapped = post.getScraps().stream()
			.anyMatch(scrap -> scrap.getUser().equals(currentUser));

		boolean editable = post.getUser().equals(currentUser);

		return PostDetailResponseDTO.builder()
			.postId(post.getId())
			.title(post.getTitle())
			.contentMarkdown(post.getContentMarkdown())
			.thumbnailUrl(post.getImages().get(PostGenFile.TypeCode.THUMBNAIL).getPublicUrl())
			.authorNickname(post.getUser().getDisplayNickname())
			.authorProfileImageUrl(post.getUser().getProfileImageUrl256())
			.likeCount(post.getLikes().size())
			.scrapCount(post.getScraps().size())
			.commentCount(post.getComments().size())
			.liked(liked)
			.scrapped(scrapped)
			.editable(editable)
			.createdAt(post.getCreatedAt())
			.tagNames(post.getPostTags().stream()
				.map(pt -> pt.getTag().getName())
				.toList())
			.comments(commentDTOs)
			.build();
	}
}