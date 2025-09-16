package com.sjy.LitHub.file.entity;

import static com.sjy.LitHub.global.model.BaseResponseStatus.*;

import java.util.Arrays;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
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
	name = "user_gen_file",
	indexes = {
		@Index(name = "idx_user_gen_file_user_id_type_code", columnList = "user_id, typeCode")
	}
)
public class UserGenFile extends GenFile {

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "type_code", length = 20, nullable = false)
	private TypeCode typeCode;

	@Override
	protected long getOwnerModelId() {
		return user.getId();
	}

	@Override
	protected String buildStorageKey(long userId, int size) {
		return FileConstant.s3UserKey(userId, size + "." + getFileExt());
	}

	@Getter
	public enum TypeCode {
		PROFILE_256(256),
		PROFILE_512(512);

		private final int size;

		TypeCode(int size) { this.size = size; }

		public static TypeCode fromSize(int size) {
			return Arrays.stream(values())
				.filter(tc -> tc.size == size)
				.findFirst()
				.orElseThrow(() -> new InvalidFileException(INVALID_FILE_TYPE));
		}
	}
}