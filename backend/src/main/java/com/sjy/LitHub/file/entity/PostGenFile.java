package com.sjy.LitHub.file.entity;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.util.FileConstant;
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
	name = "post_gen_file",
	indexes = {
		@Index(name = "idx_post_id", columnList = "post_id"),
		@Index(name = "idx_post_id_type_code", columnList = "post_id, typeCode")
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
	public long getOwnerModelId() {
		return user.getId();
	}

	@Override
	protected String buildStorageKey(long ownerId, int size) {
		if (typeCode == TypeCode.THUMBNAIL) {
			return FileConstant.s3PostKey(ownerId, size + "." + getFileExt());
		} else {
			return FileConstant.s3PostKey(ownerId, getFileNo() + "." + getFileExt());
		}
	}
}