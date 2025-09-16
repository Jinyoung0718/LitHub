package com.sjy.LitHub.global.config.securityconfig.oauth2config.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "custom.oauth.naver")
@Data
public class NaverOAuthProperties {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}