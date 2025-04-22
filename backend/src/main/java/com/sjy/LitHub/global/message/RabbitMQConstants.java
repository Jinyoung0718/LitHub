package com.sjy.LitHub.global.message;

public final class RabbitMQConstants {
	public static final String FANOUT_QUEUE = "fanout.queue";
	public static final String FANOUT_EXCHANGE = "fanout.exchange";
	public static final String FANOUT_DLX = "fanout.dlx";
	public static final String FANOUT_DLQ = "fanout.dlq";
	public static final String FANOUT_DLQ_ROUTING_KEY = "fanout.dlq";

	public static final String ARG_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
	public static final String ARG_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
}