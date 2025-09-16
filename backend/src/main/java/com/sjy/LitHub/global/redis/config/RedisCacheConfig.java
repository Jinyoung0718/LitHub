package com.sjy.LitHub.global.redis.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PreDestroy;

@Configuration
@Profile({"dev", "prod"})
public class RedisCacheConfig {

	@Value("${custom.redis.cache.host}")
	private String host;

	@Value("${custom.redis.cache.port}")
	private int port;

	@Value("${custom.redis.cache.password:}")
	private String password;

	private RedisClient redisClient;
	private StatefulRedisConnection<String, byte[]> redisConnection;

	@Bean(name = "CacheRedisConnectionFactory")
	public RedisConnectionFactory cacheRedisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

		if (password != null && !password.isEmpty()) {
			config.setPassword(RedisPassword.of(password));
		}

		return new LettuceConnectionFactory(config);
	}

	@Bean(name = "CachingStringRedisTemplate")
	public StringRedisTemplate cachingStringRedisTemplate(
		@Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory factory
	) {
		return new StringRedisTemplate(factory);
	}

	@Bean(name = "CacheRedisTemplate")
	public RedisTemplate<String, Object> cacheRedisTemplate(
		@Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory factory,
		ObjectMapper redisObjectMapper
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
		return template;
	}

	@Bean(name = "StreamRedisTemplate")
	public RedisTemplate<String, Object> streamRedisTemplate(
		@Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory factory,
		ObjectMapper redisObjectMapper
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
		return template;
	}

	@Bean
	public ProxyManager<String> lettuceBasedProxyManager() {
		RedisURI uri = RedisURI.builder()
			.withHost(host)
			.withPort(port)
			.withPassword(password == null || password.isEmpty()
				? new char[0]
				: password.toCharArray())
			.build();

		this.redisClient = RedisClient.create(uri);
		this.redisConnection = redisClient.connect(
			RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)
		);

		return LettuceBasedProxyManager.builderFor(redisConnection)
			.withExpirationStrategy(
				ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1L))
			)
			.build();
	}

	@PreDestroy
	public void cleanUp() {
		if (redisConnection != null) {
			redisConnection.close();
		}
		if (redisClient != null) {
			redisClient.shutdown();
		}
	}
}