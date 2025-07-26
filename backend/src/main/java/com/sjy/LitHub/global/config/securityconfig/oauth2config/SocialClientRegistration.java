package com.sjy.LitHub.global.config.securityconfig.oauth2config;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.config.AppConfig;

@Component
public class SocialClientRegistration {

	public ClientRegistration naverClientRegistration() {
		return ClientRegistration.withRegistrationId("naver")
			.clientId(AppConfig.naverClientId)
			.clientSecret(AppConfig.naverClientSecret)
			.redirectUri(AppConfig.naverRedirectUri)
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
			.clientId(AppConfig.googleClientId)
			.clientSecret(AppConfig.googleClientSecret)
			.redirectUri(AppConfig.googleRedirectUri)
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