package com.sjy.LitHub.file.entity;

import com.sjy.LitHub.account.entity.User;

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
		@Index(name = "idx_user_gen_file_user_id_type_code", columnList = "user_id, type_code")
	}
)
public class UserGenFile extends GenFile {

	public enum TypeCode {
		PROFILE_256,
		PROFILE_512
	}

	@ManyToOne(fetch = FetchType.LAZY)
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