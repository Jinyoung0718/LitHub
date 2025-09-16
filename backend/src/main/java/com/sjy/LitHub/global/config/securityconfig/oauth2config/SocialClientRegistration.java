package com.sjy.LitHub.global.config.securityconfig.oauth2config;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.config.securityconfig.oauth2config.util.GoogleOAuthProperties;
import com.sjy.LitHub.global.config.securityconfig.oauth2config.util.NaverOAuthProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SocialClientRegistration {

	private final GoogleOAuthProperties google;
	private final NaverOAuthProperties naver;

	public ClientRegistration naverClientRegistration() {
		return ClientRegistration.withRegistrationId("naver")
			.clientId(naver.getClientId())
			.clientSecret(naver.getClientSecret())
			.redirectUri(naver.getRedirectUri())
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.scope("email")
			.authorizationUri("https://nid.naver.com/oauth2.0/authorize")
			.tokenUri("https://nid.naver.com/oauth2.0/token")
			.userInfoUri("https://openapi.naver.com/v1/nid/me")
			.userNameAttributeName("response")
			.build();
	}

	public ClientRegistration googleClientRegistration() {
		return ClientRegistration.withRegistrationId("google")
			.clientId(google.getClientId())
			.clientSecret(google.getClientSecret())
			.redirectUri(google.getRedirectUri())
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.scope("email")
			.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
			.tokenUri("https://www.googleapis.com/oauth2/v4/token")
			.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
			.issuerUri("https://accounts.google.com")
			.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
			.userNameAttributeName(IdTokenClaimNames.SUB)
			.build();
	}
}