package com.sjy.LitHub.account.model.res;

import com.sjy.LitHub.file.util.FileConstant;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class UserBriefDTO {

	@NonNull
	private final Long userId;

	@NonNull
	private final String nickname;

	private final String profileImageUrl;

	@SuppressWarnings("unused")
	public UserBriefDTO(Long userId, String nickname, String storageKey) {
		this.userId = userId;
		this.nickname = nickname;
		this.profileImageUrl = storageKey != null ? FileConstant.publicUrl(storageKey) : null;
	}
}