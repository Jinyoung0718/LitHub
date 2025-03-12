package com.sjy.LitHub.account.repository;

import com.sjy.LitHub.account.entity.OAuthUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    boolean existsByUserAndProvider(User user, ProviderInfo provider);
    // 특정 User 가 특정 소셜 Provider 계정을 가지고 있는지 확인

    @Query("SELECT o FROM OAuthUser o WHERE o.user.userEmail = :email AND o.provider = :provider")
    Optional<OAuthUser> findByUserEmailAndProvider(@Param("email") String email, @Param("provider") ProviderInfo provider);
}