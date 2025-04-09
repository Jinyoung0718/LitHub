package com.sjy.LitHub.account.repository.user.command;

import java.time.LocalDateTime;

public interface UserCommandRepository {

	void updatePasswordById(Long userId, String newPassword);

	void restoreUserByEmail(String email);

	void deleteUserById(Long userId, LocalDateTime deletedAt);
}