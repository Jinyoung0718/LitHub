package com.sjy.LitHub;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public abstract class TestContainerConfig {

	private static final MySQLContainer<?> MYSQL_CONTAINER;
	private static final GenericContainer<?> REDIS_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8"))
			.withUsername("root")
			.withPassword("")
			.withReuse(true);
		MYSQL_CONTAINER.start();

		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:latest"))
			.withExposedPorts(6379)
			.withReuse(true);
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	static void configureDataSource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
		registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);

		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
	}
}
