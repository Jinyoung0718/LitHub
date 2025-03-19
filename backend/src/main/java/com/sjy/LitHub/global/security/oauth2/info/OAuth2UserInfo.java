package com.sjy.LitHub.global.security.oauth2.info;

import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2UserInfo {

    protected final Map<String, Object> attributes;

    protected final ProviderInfo providerInfo;

    public String getProviderId() {
        return attributes.get(providerInfo.getProviderCode()).toString();
    }

    public String getEmail() {
        return attributes.get(providerInfo.getIdentifier()).toString();
    }
}