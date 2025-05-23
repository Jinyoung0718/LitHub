package com.sjy.LitHub.global.security.oauth2.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfo;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfoFactory;
import com.sjy.LitHub.global.security.oauth2.service.login.OAuthUserLoginService;
import com.sjy.LitHub.global.security.oauth2.service.signup.OAuthSignupRedirectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthUserLoginService oAuthUserLoginService;
    private final OAuthSignupRedirectService oAuthSignupRedirectService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        ProviderInfo provider = ProviderInfo.from(userRequest.getClientRegistration().getRegistrationId());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        Optional<User> userOpt = userRepository.findByUserEmailAll(oAuth2UserInfo.getEmail());
        if (userOpt.isPresent()) {
            return oAuthUserLoginService.restoreAndLinkOAuthUser(userOpt.get(), oAuth2UserInfo, provider);
        }

        return oAuthSignupRedirectService.handleNewUser(oAuth2UserInfo, provider);
    }
}