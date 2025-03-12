package com.sjy.LitHub.global.security.oauth2.info;

import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.global.security.oauth2.user.GoogleOAuth2User;
import com.sjy.LitHub.global.security.oauth2.user.NaverOAuth2User;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderInfo provider, Map<String, Object> attributes) {
        return switch (provider) {
            case NAVER -> new NaverOAuth2User(attributes);
            case GOOGLE -> new GoogleOAuth2User(attributes);
        };
    }
}