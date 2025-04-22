package com.sjy.LitHub.file.entity;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.post.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name = "post_gen_image",
	indexes = {
		@Index(name = "idx_post_id", columnList = "post_id"), // 게시글 ID 기반 이미지 조회 (대표 썸네일, 마크다운)
		@Index(name = "idx_file_name_user_id_post", columnList = "fileName, user_id, post_id"), // 임시 파일 처리 (fileName 기반 + post 연결 여부)
		@Index(name = "idx_user_id_type_code", columnList = "user_id, type_code"), // 사용자의 특정 타입 이미지 검색 (예: 마크다운)
		@Index(name = "idx_post_id_type_code", columnList = "post_id, type_code") // 게시글의 썸네일/마크다운 이미지 구분 조회
	}
)
public class PostGenFile extends GenFile {

	public enum TypeCode {
		THUMBNAIL,
		MARKDOWN
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private TypeCode typeCode;

	@Override
	protected long getOwnerModelId() {
		return user.getId();
	}

	@Override
	protected String getTypeCodeAsStr() {
		return typeCode.name().toLowerCase();
	}
}