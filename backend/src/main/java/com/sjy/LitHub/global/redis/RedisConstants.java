package com.sjy.LitHub.global.redis;

public final class RedisConstants {

	public static final String TIMER_EVENT_STREAM = "stream:room:timer:events";
	public static final String WAITING_ROOM_EVENT_STREAM = "stream:waiting-room:events";

	public static final String API_POSTS_LIKES  = "/api/posts/likes";
	public static final String API_POSTS_SCRAPS = "/api/posts/scraps";

	private RedisConstants() {}
}
