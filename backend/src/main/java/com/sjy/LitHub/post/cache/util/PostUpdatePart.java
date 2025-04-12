package com.sjy.LitHub.post.cache.util;

import com.sjy.LitHub.post.model.res.CommentResponseDTO;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;

public enum PostUpdatePart {

	ADD_COMMENT {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			CommentResponseDTO comment = (CommentResponseDTO) data;
			dto.getComments().add(comment);
			dto.setCommentCount(dto.getCommentCount() + 1);
		}
	},

	REMOVE_COMMENT {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			Long commentId = (Long) data;
			dto.getComments().removeIf(c -> c.getId().equals(commentId));
			dto.setCommentCount(dto.getCommentCount() - 1);
		}
	},

	EDIT_COMMENT {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			CommentResponseDTO updated = (CommentResponseDTO) data;
			dto.getComments().stream()
				.filter(c -> c.getId().equals(updated.getId()))
				.findFirst()
				.ifPresent(c -> c.setContent(updated.getContent()));
		}
	},

	TOGGLE_LIKE {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			boolean liked = !dto.isLiked();
			dto.setLiked(liked);
			dto.setLikeCount(dto.getLikeCount() + (liked ? 1 : -1));
		}
	},

	TOGGLE_SCRAP {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			boolean scrapped = !dto.isScrapped();
			dto.setScrapped(scrapped);
			dto.setScrapCount(dto.getScrapCount() + (scrapped ? 1 : -1));
		}
	};

	public abstract void apply(PostDetailResponseDTO dto, Object data);
}
