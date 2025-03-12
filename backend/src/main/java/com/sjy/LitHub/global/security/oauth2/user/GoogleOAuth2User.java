package com.sjy.LitHub.global.security.oauth2.user;

import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfo;

import java.util.Map;

public class GoogleOAuth2User extends OAuth2UserInfo {
    public GoogleOAuth2User(Map<String, Object> attributes) {
        super(attributes, ProviderInfo.GOOGLE);
    }
}