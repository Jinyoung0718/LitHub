package com.sjy.LitHub.file.util;

import java.util.Set;

import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.global.config.AppConfig;

public final class FileConstant {

	private FileConstant() {}

	public static final String USER_DIR_PREFIX = "userGenFile";

	public static final String POST_DIR_PREFIX = "postGenFile";

	public static final String BASE_PROFILE_DIR = "baseprofile";

	public static final String ORIGINAL_FILE_NAME_SEPARATOR = "--originalFileName_";
	public static final long DEFAULT_SIZE = 10 * 1024 * 1024L;

	public static final Set<String> RAW_EXTENSIONS = Set.of("gif", "svg");

	public static String localBaseProfilePath(int size) {
		return AppConfig.getFileUploadDir() + "/" + BASE_PROFILE_DIR + "/base-profile_" + size + ".webp";
	}

	public static String localProfileImagePath(Long userId, int size) {
		String dir = AppConfig.getFileUploadDir() + "/gen/" + USER_DIR_PREFIX + "/" + userId;
		LocalFileUtil.mkdir(dir);
		return dir + "/" + size + ".webp";
	}

	public static String s3UserKey(Long userId, String fileName) {
		return USER_DIR_PREFIX + "/" + userId + "/" + fileName;
	}

	public static String s3PostKey(Long postId, String fileName) {
		return POST_DIR_PREFIX + "/" + postId + "/" + fileName;
	}

	public static String s3BaseProfileKey(int size) {
		return BASE_PROFILE_DIR + "/base-profile_" + size + ".webp";
	}

	public static String publicUrl(String key) {
		if (AppConfig.isProd()) {
			return "https://" + AppConfig.getS3Bucket()
				+ ".s3.ap-northeast-2.amazonaws.com/"
				+ key;
		}
		return AppConfig.getSiteBackUrl() + "/gen/" + key;
	}

	public static String toLocalPath(String storageKey) {
		return AppConfig.getFileUploadDir() + "/gen/" + storageKey;
	}
}