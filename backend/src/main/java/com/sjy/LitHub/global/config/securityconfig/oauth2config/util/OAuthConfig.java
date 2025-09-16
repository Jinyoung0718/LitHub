package com.sjy.LitHub.global.config.securityconfig.oauth2config.util;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GoogleOAuthProperties.class, NaverOAuthProperties.class})
public class OAuthConfig {}