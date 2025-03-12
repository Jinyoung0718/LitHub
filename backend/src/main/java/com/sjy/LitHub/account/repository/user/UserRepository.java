package com.sjy.LitHub.account.repository.user;

import com.sjy.LitHub.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    @Query("SELECT u FROM User u WHERE u.userEmail = :userEmail")
    Optional<User> findByUserEmailAll(@Param("userEmail") String userEmail);

    @Query("SELECT u FROM User u WHERE u.userEmail = :userEmail AND u.deletedAt IS NULL")
    Optional<User> findByUserEmailActive(@Param("userEmail") String userEmail);

    @Query("SELECT u.password FROM User u WHERE u.id = :userId")
    String findPasswordById(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
    void updatePasswordById(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    @Modifying
    @Query("UPDATE User u SET u.profileImageUrlSmall = :smallImageUrl, u.profileImageUrlLarge = :largeImageUrl WHERE u.id = :userId")
    void updateUserProfileImage(@Param("userId") String userId,
                                @Param("smallImageUrl") String smallImageUrl,
                                @Param("largeImageUrl") String largeImageUrl);

    boolean existsByNickName(@Param("nickName") String nickName);

    @Modifying
    @Query("UPDATE User u SET u.deletedAt = NULL WHERE u.userEmail = :email AND u.deletedAt IS NOT NULL")
    void restoreUserByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.profileImageUrlSmall = :smallImageUrl, u.profileImageUrlLarge = :largeImageUrl WHERE u.id = :userId")
    void resetUserProfileImage(@Param("userId") String userId,
                               @Param("smallImageUrl") String smallImageUrl,
                               @Param("largeImageUrl") String largeImageUrl);
}