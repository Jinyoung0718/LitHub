package com.sjy.LitHub.global.config.securityconfig.oauth2config;

import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SocialClientRegistration {

	private final Environment environment;

	public ClientRegistration naverClientRegistration() {
		return ClientRegistration.withRegistrationId("naver")
			.clientId(environment.getProperty("NAVER_CLIENT_ID_DEV"))
			.clientSecret(environment.getProperty("NAVER_CLIENT_SECRET_DEV"))
			.redirectUri(environment.getProperty("NAVER_REDIRECT_URI_DEV"))
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
			.clientId(environment.getProperty("GOOGLE_CLIENT_ID_DEV"))
			.clientSecret(environment.getProperty("GOOGLE_CLIENT_SECRET_DEV"))
			.redirectUri(environment.getProperty("GOOGLE_REDIRECT_URI_DEV"))
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