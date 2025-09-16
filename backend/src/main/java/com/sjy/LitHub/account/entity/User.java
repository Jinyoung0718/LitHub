package com.sjy.LitHub.account.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.global.entity.BaseTime;
import com.sjy.LitHub.post.entity.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", indexes = {
	@Index(name = "idx_user_email_deleted", columnList = "user_email, deleted_at"), // 이메일 기반 계정 상태 조회
	@Index(name = "idx_user_nickname", columnList = "user_nickname") // 닉네임 중복 여부 확인용
})
public class User extends BaseTime {

	@Column(name = "user_email", nullable = false, unique = true, length = 100)
	private String userEmail;

	@Column(name = "user_nickname", nullable = false, unique = true, length = 50)
	private String nickName;

	@Column(name = "user_password", nullable = false, length = 60)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "tier", nullable = false, length = 10)
	@Builder.Default
	private Tier tier = Tier.BRONZE;

	@Column(name = "points", nullable = false)
	@Builder.Default
	private int point = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 10)
	@Builder.Default
	private Role role = Role.ROLE_USER;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKey(name = "typeCode")
	@Builder.Default
	@Setter
	private Map<UserGenFile.TypeCode, UserGenFile> userGenFiles = new HashMap<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Post> posts = new ArrayList<>();

	@Column(name = "follower_count", nullable = false)
	@Builder.Default
	private long followerCount = 0L;

	public void addUserGenFile(UserGenFile file) {
		file.setUser(this);
		userGenFiles.put(file.getTypeCode(), file);
	}

	public String getProfileImageUrl512() {
		return Optional.ofNullable(userGenFiles.get(UserGenFile.TypeCode.PROFILE_512))
			.map(UserGenFile::getPublicUrl)
			.orElse(null);
	}

	public String getProfileImageUrl256() {
		return Optional.ofNullable(userGenFiles.get(UserGenFile.TypeCode.PROFILE_256))
			.map(UserGenFile::getPublicUrl)
			.orElse(null);
	}

	public void encodePassword(BCryptPasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(this.password);
	}

	public String getDisplayNickname() {
		return this.deletedAt != null ? "탈퇴한 사용자" : this.nickName;
	}

	public void replaceUserGenFiles(Map<UserGenFile.TypeCode, UserGenFile> newFiles) {
		this.userGenFiles = new HashMap<>(newFiles);
		this.userGenFiles.values().forEach(file -> file.setUser(this));
	}

	public User(Long id) {
		this.setId(id);
	}
}