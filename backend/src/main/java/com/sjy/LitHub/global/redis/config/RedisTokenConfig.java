package com.sjy.LitHub.global.redis.config;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@Profile({"dev", "prod"})
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisTokenConfig {

	private final RedisProperties redisProperties;

	@Bean(name = "TokenRedisConnectionFactory")
	public RedisConnectionFactory tokenRedisConnectionFactory() {
		if (isSentinelMode()) {
			return new LettuceConnectionFactory(createSentinelConfig());
		}

		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	@Bean(name = "TokenStringRedisTemplate")
	public RedisTemplate<String, String> tokenStringRedisTemplate(
		@Qualifier("TokenRedisConnectionFactory") RedisConnectionFactory factory
	) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean(name = "TokenJsonRedisTemplate")
	public RedisTemplate<String, Object> tokenJsonRedisTemplate(
		@Qualifier("TokenRedisConnectionFactory") RedisConnectionFactory factory,
		ObjectMapper redisObjectMapper
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
		return template;
	}

	private boolean isSentinelMode() {
		return redisProperties.getSentinel() != null
			&& redisProperties.getSentinel().getMaster() != null;
	}

	private RedisSentinelConfiguration createSentinelConfig() {
		RedisSentinelConfiguration config = new RedisSentinelConfiguration(
			redisProperties.getSentinel().getMaster(),
			new HashSet<>(redisProperties.getSentinel().getNodes())
		);

		if (redisProperties.getPassword() != null) {
			config.setPassword(redisProperties.getPassword());
		}

		return config;
	}

}