package com.sjy.LitHub.global.config;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
public class AppConfig {

	@Getter
	private static Tika tika;

	@Getter
	private static String siteBackUrl;

	@Getter
	public static String fileUploadDir;

	@Value("${custom.site.backUrl}")
	public void setSiteBackUrl(String siteBackUrl) {
		AppConfig.siteBackUrl = siteBackUrl;
	}

	@Value("${file.upload-dir}")
	public void setFileDirPath(String fileUploadDir) {
		AppConfig.fileUploadDir = fileUploadDir;
	}

	public static String getSiteFrontUrl() {
		return "http://localhost:5173";
	}

	@Autowired
	public void setTika(Tika tika) {
		AppConfig.tika = tika;
	}

	@Getter
	private static String customMaxImageSize;

	@Value("${custom.upload.max-image-size}")
	public void setCustomMaxImageSize(String size) {
		AppConfig.customMaxImageSize = size;
	}

	@Getter
	private static String customMaxFileSize;

	@Value("${custom.upload.max-file-size}")
	public void setCustomMaxFileSize(String size) {
		AppConfig.customMaxFileSize = size;
	}
}