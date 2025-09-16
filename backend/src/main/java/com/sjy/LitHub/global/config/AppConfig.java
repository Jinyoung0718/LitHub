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

	// ---------------- S3 설정 ----------------
	@Getter
	private static String s3Bucket;

	@Getter
	private static String s3BaseProfileDir;

	@Value("${custom.s3.bucket}")
	public void setS3Bucket(String bucket) {
		AppConfig.s3Bucket = bucket;
	}

	@Value("${custom.s3.base-profile-dir}")
	public void setS3BaseProfileDir(String baseDir) {
		AppConfig.s3BaseProfileDir = baseDir;
	}

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


	// ---------------- 기타 주입 ----------------

	@Autowired
	public void setTika(Tika tika) {
		AppConfig.tika = tika;
	}
}