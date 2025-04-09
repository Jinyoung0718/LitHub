package com.sjy.LitHub.account.repository.user.command;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCommandRepositoryImpl implements UserCommandRepository {

	private final EntityManager em;

	@Override
	public void updatePasswordById(Long userId, String newPassword) {
		em.createQuery("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
			.setParameter("newPassword", newPassword)
			.setParameter("userId", userId)
			.executeUpdate();
	}

	@Override
	public void restoreUserByEmail(String email) {
		em.createQuery("UPDATE User u SET u.deletedAt = NULL WHERE u.userEmail = :email AND u.deletedAt IS NOT NULL")
			.setParameter("email", email)
			.executeUpdate();
	}

	@Override
	public void deleteUserById(Long userId, LocalDateTime deletedAt) {
		em.createQuery("UPDATE User u SET u.deletedAt = :deletedAt WHERE u.id = :userId AND u.deletedAt IS NULL")
			.setParameter("deletedAt", deletedAt)
			.setParameter("userId", userId)
			.executeUpdate();
	}
}