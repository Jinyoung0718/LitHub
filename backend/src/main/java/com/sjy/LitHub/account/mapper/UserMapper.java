package com.sjy.LitHub.account.mapper;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.OAuthUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.model.req.signup.SignupDTO;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    // 자체 회원가입 시 User 변환
    public User ofSignupDTO(SignupDTO signupDTO) {
        return User.builder()
            .nickName(signupDTO.getNickName())
            .userEmail(signupDTO.getUserEmail())
            .password(signupDTO.getUserPassword())
            .role(Role.ROLE_USER)
            .build();
    }

    // 기존 회원이 추가 소셜 계정을 연동하는 경우
    public OAuthUser ofOAuthAccountForExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo,
        ProviderInfo provider) {
        return OAuthUser.builder()
            .user(existingUser)
            .provider(provider)
            .identifier(oAuth2UserInfo.getProviderId())
            .build();
    }

    // 소셜 로그인 후 회원가입 완료 시 User 변환 - (소셜로 회원가입)
    public User ofSocialSignupDTO(SocialSignupDTO socialSignupDto, String encodedPassword, String email) {
        return User.builder()
            .nickName(socialSignupDto.getNickName())
            .password(encodedPassword)
            .userEmail(email)
            .role(Role.ROLE_USER)
            .build();
    }

    // 회원가입 완료 시 OAuthUser 변환 - (소셜로 회원가입)
    public OAuthUser ofOAuthSignupComplete(User user, ProviderInfo provider, String providerId) {
        return OAuthUser.builder()
            .user(user)
            .provider(provider)
            .identifier(providerId)
            .build();
    }
}