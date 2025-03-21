package com.sjy.LitHub.account.repository.user.command;

import java.time.LocalDateTime;

public interface UserCommandRepository {

	void updatePasswordById(Long userId, String newPassword);

	void updateUserProfileImage(String userId, String smallImageUrl, String largeImageUrl);

	void resetUserProfileImage(String userId, String smallImageUrl, String largeImageUrl);

	void restoreUserByEmail(String email);

	void deleteUserById(Long userId, LocalDateTime deletedAt);

	void deletePhysicallyById(Long userId);
}