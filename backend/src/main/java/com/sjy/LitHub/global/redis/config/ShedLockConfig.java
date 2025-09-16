package com.sjy.LitHub.global.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10s")
public class ShedLockConfig {

	private final @Qualifier("CacheRedisConnectionFactory")
	RedisConnectionFactory cacheRedisConnectionFactory;

	public ShedLockConfig(
		@Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory cacheRedisConnectionFactory
	) {
		this.cacheRedisConnectionFactory = cacheRedisConnectionFactory;
	}

	@Bean
	public LockProvider lockProvider() {
		return new RedisLockProvider(cacheRedisConnectionFactory, "shedlock:locks");
	}
}