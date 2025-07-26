package com.sjy.LitHub.global.config;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import lombok.Getter;

@EnableRetry
@Configuration
public class AppConfig {

	@Getter
	private static boolean isProd;

	@Getter
	private static Tika tika;

	@Getter
	private static String siteBackUrl;

	@Getter
	private static String siteFrontUrl;

	@Getter
	private static String fileUploadDir;

	@Getter
	private static String customMaxImageSize;

	@Getter
	private static String customMaxFileSize;

	@Getter
	private static String siteDomain;

	@Getter
	private static boolean cookieSecure;

	@Getter
	private static boolean cookieHttpOnly;

	@Getter
	private static String cookieSameSite;

	@Getter
	public static String googleClientId;

	@Getter
	public static String googleClientSecret;

	@Getter
	public static String googleRedirectUri;

	@Getter
	public static String naverClientId;

	@Getter
	public static String naverClientSecret;

	@Getter
	public static String naverRedirectUri;

	// ---------------- 기본 설정 ----------------

	@Value("${spring.profiles.active}")
	public void setIsProd(String profile) {
		AppConfig.isProd = "prod".equalsIgnoreCase(profile);
	}

	@Value("${custom.site.back-url}")
	public void setSiteBackUrl(String siteBackUrl) {
		AppConfig.siteBackUrl = siteBackUrl;
	}

	@Value("${custom.site.front-url}")
	public void setSiteFrontUrl(String siteFrontUrl) {
		AppConfig.siteFrontUrl = siteFrontUrl;
	}

	@Value("${custom.file.upload-dir}")
	public void setFileUploadDir(String fileUploadDir) {
		AppConfig.fileUploadDir = fileUploadDir;
	}

	@Value("${custom.upload.max-image-size}")
	public void setCustomMaxImageSize(String size) {
		AppConfig.customMaxImageSize = size;
	}

	@Value("${custom.upload.max-file-size}")
	public void setCustomMaxFileSize(String size) {
		AppConfig.customMaxFileSize = size;
	}

	@Value("${custom.site.domain}")
	public void setSiteDomain(String siteDomain) {
		AppConfig.siteDomain = siteDomain;
	}

	@Value("${custom.cookie.secure}")
	public void setCookieSecure(boolean secure) {
		AppConfig.cookieSecure = secure;
	}

	@Value("${custom.cookie.http-only}")
	public void setCookieHttpOnly(boolean httpOnly) {
		AppConfig.cookieHttpOnly = httpOnly;
	}

	@Value("${custom.cookie.same-site}")
	public void setCookieSameSite(String sameSite) {
		AppConfig.cookieSameSite = sameSite;
	}

	// ---------------- OAuth 설정 ----------------

	@Value("${custom.google-oauth.client-id}")
	public void setGoogleClientId(String value) {
		AppConfig.googleClientId = value;
	}

	@Value("${custom.google-oauth.client-secret}")
	public void setGoogleClientSecret(String value) {
		AppConfig.googleClientSecret = value;
	}

	@Value("${custom.naver-oauth.client-id}")
	public void setNaverClientId(String value) {
		AppConfig.naverClientId = value;
	}

	@Value("${custom.naver-oauth.client-secret}")
	public void setNaverClientSecret(String value) {
		AppConfig.naverClientSecret = value;
	}

	@Value("${custom.${spring.profiles.active}.oauth.google.redirect-uri}")
	public void setGoogleRedirectUri(String value) {
		AppConfig.googleRedirectUri = value;
	}

	@Value("${custom.${spring.profiles.active}.oauth.naver.redirect-uri}")
	public void setNaverRedirectUri(String value) {
		AppConfig.naverRedirectUri = value;
	}

	// ---------------- 기타 주입 ----------------

	@Autowired
	public void setTika(Tika tika) {
		AppConfig.tika = tika;
	}
}