package com.sjy.LitHub.record.service.timer.util;

import java.time.Duration;

public final class TimerConstants {

	// Redis 해시 키 포맷
	public static final String TIMER_HASH_KEY_FORMAT = "room:%d:timer";

	// Redis 참여자 Set 키 포맷
	public static final String TIMER_PARTICIPANT_SET_KEY_FORMAT = "room:%d:participants";

	// Redis 하트비트 키
	public static final String HEARTBEAT_KEY_FORMAT = "room:%d:owner:heartbeat";
	public static final String ROOMS_HEARTBEAT_ZSET = "rooms:heartbeat:exp";

	// 만료 처리 마커
	public static final String ROOM_CLOSED_MARKER_FORMAT = "room:%d:closed";

	// 타이머 필드명
	public static final String TIMER_FIELD_START_TIME = "startTime";
	public static final String TIMER_FIELD_OWNER_ID = "ownerId";
	public static final String TIMER_FIELD_PAUSED = "paused";
	public static final String TIMER_FIELD_PAUSE_START_TIME = "pauseStartTime";
	public static final String TIMER_FIELD_ACCUMULATED_MINUTES = "accumulatedMinutes";

	// Pub/Sub 토픽 및 이벤트명
	public static final String TIMER_EVENT_TOPIC = "room:timer:events";
	public static final String WAITING_ROOM_EVENT_TOPIC = "waiting-room:event";

	// SSE 이벤트명
	public static final String EVENT_NAME_ROOM_META_CHANGED = "room-meta-event";
	public static final String EVENT_NAME_TIMER_STATUS_CHANGED = "timer-status-changed";

	// 스케줄러/락 튜닝 값
	public static final long REAPER_FIXED_DELAY_MS = 10_000L;
	public static final int    REAPER_BATCH_SIZE     = 500;
	public static final String REAPER_LOCK_AT_MOST   = "10s";
	public static final String REAPER_LOCK_AT_LEAST  = "2s";
	public static final String REAPER_NAME = "roomsHeartbeatReaper";

	// 초대 알림 키 포맷
	public static final String INVITE_KEY_FORMAT       = "invite:user:%d:room:%d";
	public static final String INVITE_INDEX_KEY_FORMAT = "invite:user:%d:index";
	public static final Duration INVITE_TTL = Duration.ofHours(1);

	// SSE 관련
	public static final String HEADER_LAST_EVENT_ID = "Last-Event-ID";

	private TimerConstants() {
	}
}