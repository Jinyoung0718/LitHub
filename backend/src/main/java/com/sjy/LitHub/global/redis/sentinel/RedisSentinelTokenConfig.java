package com.sjy.LitHub.global.redis.sentinel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class RedisSentinelTokenConfig {

	private final ObjectMapper redisObjectMapper;
	private final RedisSentinelProperties sentinelProperties;

	@Bean(name = "sentinelRedisConnectionFactory")
	public RedisConnectionFactory sentinelRedisConnectionFactory() {
		RedisSentinelConfiguration config = new RedisSentinelConfiguration();
		config.master(sentinelProperties.getMaster());

		for (String node : sentinelProperties.getNodes()) {
			try {
				String[] parts = node.split(":");
				config.sentinel(parts[0], Integer.parseInt(parts[1]));
			} catch (Exception e) {
				log.error("[Redis Sentinel] 노드 파싱 실패: {}", node, e);
			}
		}

		return new LettuceConnectionFactory(config);
	}

	@Bean(name = "TokenStringRedisTemplate")
	public RedisTemplate<String, String> sentinelStringRedisTemplate(
		@Qualifier("sentinelRedisConnectionFactory") RedisConnectionFactory factory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

	@Bean(name = "TokenJsonRedisTemplate")
	public RedisTemplate<String, Object> sentinelJsonRedisTemplate(
		@Qualifier("sentinelRedisConnectionFactory") RedisConnectionFactory factory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}
}