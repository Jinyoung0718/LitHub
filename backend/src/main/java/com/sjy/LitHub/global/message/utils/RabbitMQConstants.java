package com.sjy.LitHub.global.message.utils;

public final class RabbitMQConstants {

	// Fanout 관련 상수
	public static final String FANOUT_QUEUE = "fanout.queue";
	public static final String FANOUT_EXCHANGE = "fanout.exchange";
	public static final String FANOUT_DLX = "fanout.dlx";
	public static final String FANOUT_DLQ = "fanout.dlq";
	public static final String FANOUT_DLQ_ROUTING_KEY = "fanout.dlq";

	public static final String ARG_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
	public static final String ARG_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

	// Fanout 처리 정책
	public static final int FANOUT_PROCESSED_KEY_TTL_SECONDS = 300;
	public static final int FANOUT_MAX_FEED_SIZE = 500;
	public static final int FANOUT_FEED_TTL_DAYS = 30;

	// 게시글 삭제 관련
	public static final String POST_DELETED_EXCHANGE = "post.deleted.exchange";
	public static final String POST_DELETED_QUEUE = "post.deleted.queue";
	public static final String POST_DELETED_ROUTING_KEY = "post.deleted";

	// Interaction 이벤트 관련
	public static final String LIKE_EXCHANGE = "interaction.like.exchange";
	public static final String LIKE_QUEUE = "interaction.like.queue";
	public static final String LIKE_ROUTING_KEY = "interaction.like";

	public static final String SCRAP_EXCHANGE = "interaction.scrap.exchange";
	public static final String SCRAP_QUEUE = "interaction.scrap.queue";
	public static final String SCRAP_ROUTING_KEY = "interaction.scrap";

	private RabbitMQConstants() {}
}