package com.sjy.LitHub.global.security.oauth2.user;

import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfo;

import java.util.Map;

@SuppressWarnings("unchecked")
public class NaverOAuth2User extends OAuth2UserInfo {
    public NaverOAuth2User(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("response"), ProviderInfo.NAVER);
    }
}