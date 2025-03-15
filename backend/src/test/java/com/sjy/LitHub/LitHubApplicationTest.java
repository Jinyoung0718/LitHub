package com.sjy.LitHub;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LitHubApplicationTest extends TestContainerConfig {

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
		log.info("🛠 현재 환경 변수 설정 확인");
		log.info("spring.datasource.url = {}", environment.getProperty("spring.datasource.url"));
		log.info("spring.datasource.username = {}", environment.getProperty("spring.datasource.username"));
		log.info("spring.datasource.password = {}", environment.getProperty("spring.datasource.password"));
		log.info("spring.datasource.driver-class-name = {}", environment.getProperty("spring.datasource.driver-class-name"));

		log.info("spring.data.redis.host = {}", environment.getProperty("spring.data.redis.host"));
		log.info("spring.data.redis.port = {}", environment.getProperty("spring.data.redis.port"));

		assertThat(environment.getProperty("spring.datasource.url")).isNotNull();
		assertThat(environment.getProperty("spring.datasource.username")).isNotNull();
		assertThat(environment.getProperty("spring.datasource.password")).isNotNull();
		assertThat(environment.getProperty("spring.datasource.driver-class-name")).isNotNull();

		assertThat(environment.getProperty("spring.data.redis.host")).isNotNull();
		assertThat(environment.getProperty("spring.data.redis.port")).isNotNull();
	}
}