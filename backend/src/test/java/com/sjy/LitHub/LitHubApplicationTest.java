package com.sjy.LitHub;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.sjy.LitHub.global.security.support.TestContainerSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
class LitHubApplicationTest extends TestContainerSupport {

	@Test
	void contextLoads() {
		log.info("MySQL 접속 URL: {}", mysql.getJdbcUrl());
		log.info("MySQL 사용자명: {}", mysql.getUsername());

		log.info("Redis 호스트: {}", redis.getHost());
		log.info("Redis 포트: {}", redis.getMappedPort(6379));

		log.info("RabbitMQ 호스트: {}", rabbitmq.getHost());
		log.info("RabbitMQ 포트: {}", rabbitmq.getMappedPort(5672));
	}
}