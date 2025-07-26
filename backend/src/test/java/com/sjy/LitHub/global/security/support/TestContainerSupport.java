package com.sjy.LitHub.global.security.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class TestContainerSupport {

	private static final String MYSQL_IMAGE = "mysql:8";
	private static final String REDIS_IMAGE = "redis:latest";
	private static final String RABBITMQ_IMAGE = "rabbitmq:3-management";

	private static final int REDIS_PORT = 6379;
	private static final int RABBITMQ_AMQP_PORT = 5672;
	private static final int RABBITMQ_HTTP_PORT = 15672;

	protected static final JdbcDatabaseContainer<?> mysql;
	protected static final GenericContainer<?> redis;
	protected static final GenericContainer<?> rabbitmq;

	static {
		mysql = new MySQLContainer<>(MYSQL_IMAGE);

		redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
			.withExposedPorts(REDIS_PORT)
			.withReuse(true);

		rabbitmq = new GenericContainer<>(DockerImageName.parse(RABBITMQ_IMAGE))
			.withExposedPorts(RABBITMQ_AMQP_PORT, RABBITMQ_HTTP_PORT)
			.withEnv("RABBITMQ_DEFAULT_USER", "testuser")
			.withEnv("RABBITMQ_DEFAULT_PASS", "testpass")
			.withReuse(true);

		mysql.start();
		redis.start();
		rabbitmq.start();
	}

	@DynamicPropertySource
	public static void overrideProperties(DynamicPropertyRegistry registry) {
		// MySQL
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);

		// Redis
		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT).toString());

		// RabbitMQ
		registry.add("spring.rabbitmq.host", rabbitmq::getHost);
		registry.add("spring.rabbitmq.port", () -> rabbitmq.getMappedPort(RABBITMQ_AMQP_PORT).toString());
		registry.add("spring.rabbitmq.username", () -> "testuser");
		registry.add("spring.rabbitmq.password", () -> "testpass");
	}
}