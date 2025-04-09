package com.sjy.LitHub.file.util;

import com.sjy.LitHub.global.config.AppConfig;

public class ProfileImageUtil {

	public static String getUserProfileDir(Long userId) {
		return AppConfig.getFileUploadDir() + "/gen/userGenFile/" + userId;
	}

	public static String copyBaseProfileToUserDir(Long userId, int size) {
		String sourceFileName = "base-profile_" + size + ".webp";
		String from = AppConfig.getFileUploadDir() + "/baseprofile/" + sourceFileName;
		String to = getUserProfileDir(userId) + "/" + size + ".webp";
		FileUtil.copy(from, to);
		return to;
	}
}