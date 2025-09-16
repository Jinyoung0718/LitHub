package com.sjy.LitHub.record.service.group;

import com.sjy.LitHub.record.service.timer.util.TimerConstants;

public final class NotificationUtil {

	private NotificationUtil() {}

	public static String inviteKey(long userId, long roomId) {
		return String.format(TimerConstants.INVITE_KEY_FORMAT, userId, roomId);
	}

	public static String inviteIndexKey(long userId) {
		return String.format(TimerConstants.INVITE_INDEX_KEY_FORMAT, userId);
	}
}