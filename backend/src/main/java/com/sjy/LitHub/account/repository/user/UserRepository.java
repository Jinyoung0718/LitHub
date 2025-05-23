package com.sjy.LitHub.account.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.command.UserCommandRepository;
import com.sjy.LitHub.account.repository.user.custom.UserRepositoryCustom;
import com.sjy.LitHub.account.repository.user.porfile.UserProfileRepositoryCustom;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
	UserCommandRepository,
	UserRepositoryCustom,
	UserProfileRepositoryCustom {

	@Query("SELECT u FROM User u WHERE u.userEmail = :userEmail")
	Optional<User> findByUserEmailAll(@Param("userEmail") String userEmail);

	@Query("SELECT u FROM User u WHERE u.userEmail = :userEmail AND u.deletedAt IS NULL")
	Optional<User> findByUserEmailActive(@Param("userEmail") String userEmail);

	@Query("SELECT u FROM User u WHERE u.userEmail = :userEmail AND u.deletedAt IS NOT NULL")
	Optional<User> findByUserEmailDeleted(@Param("userEmail") String userEmail);

	@Query("SELECT u.password FROM User u WHERE u.id = :userId")
	String findPasswordById(@Param("userId") Long userId);

	boolean existsByNickName(@Param("nickName") String nickName);
}
