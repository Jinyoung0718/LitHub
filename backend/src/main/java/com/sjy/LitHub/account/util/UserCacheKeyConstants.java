package com.sjy.LitHub.account.util;

public final class UserCacheKeyConstants {

	private UserCacheKeyConstants() {}

	public static final String PROFILE_KEY_PREFIX = "user:profile:";
	public static final String STATS_KEY_PREFIX   = "user:stats:";
	public static final String STUDY_KEY_PREFIX   = "user:studies:";

	public static String profileKey(Long userId) {
		return PROFILE_KEY_PREFIX + userId;
	}

	public static String statsKey(Long userId, int year) {
		return STATS_KEY_PREFIX + userId + ":" + year;
	}

	public static String studyKey(Long userId) {
		return STUDY_KEY_PREFIX + userId + ":recent10";
	}
}