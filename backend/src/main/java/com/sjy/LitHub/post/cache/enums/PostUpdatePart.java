package com.sjy.LitHub.post.cache.enums;

import com.sjy.LitHub.global.exception.custom.InvalidRedisException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.model.res.CommentResponseDTO;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;

public enum PostUpdatePart {

	ADD_COMMENT {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			if (!(data instanceof CommentResponseDTO comment)) {
				throw new InvalidRedisException(BaseResponseStatus.REDIS_UPDATE_TYPE_MISMATCH);
			}
			dto.getComments().add(comment);
			dto.setCommentCount(dto.getCommentCount() + 1);
		}
	},

	REMOVE_COMMENT {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			if (!(data instanceof Long commentId)) {
				throw new InvalidRedisException(BaseResponseStatus.REDIS_UPDATE_TYPE_MISMATCH);
			}
			dto.getComments().removeIf(c -> c.getId().equals(commentId));
			dto.setCommentCount(dto.getCommentCount() - 1);
		}
	},

	EDIT_COMMENT {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			if (!(data instanceof CommentResponseDTO updated)) {
				throw new InvalidRedisException(BaseResponseStatus.REDIS_UPDATE_TYPE_MISMATCH);
			}
			dto.getComments().stream()
				.filter(c -> c.getId().equals(updated.getId()))
				.findFirst()
				.ifPresent(c -> c.setContent(updated.getContent()));
		}
	},

	TOGGLE_LIKE {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			dto.setLiked(!dto.isLiked());
			dto.setLikeCount(dto.getLikeCount() + (dto.isLiked() ? 1 : -1));
		}
	},

	TOGGLE_SCRAP {
		@Override
		public void apply(PostDetailResponseDTO dto, Object data) {
			dto.setScrapped(!dto.isScrapped());
			dto.setScrapCount(dto.getScrapCount() + (dto.isScrapped() ? 1 : -1));
		}
	};

	public abstract void apply(PostDetailResponseDTO dto, Object data);
}