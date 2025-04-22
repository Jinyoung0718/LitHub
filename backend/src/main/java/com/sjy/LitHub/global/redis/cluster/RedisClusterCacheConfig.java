package com.sjy.LitHub.global.redis.cluster;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class RedisClusterCacheConfig {

	private final RedisProperties redisProperties;

	private RedisClusterClient redisClusterClient;
	private StatefulRedisClusterConnection<String, byte[]> redisClusterConnection;

	@Bean(name = "CachingRedisConnectionFactory")
	public RedisConnectionFactory cachingRedisConnectionFactory() {
		RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(
			redisProperties.getCluster().getNodes());
		return new LettuceConnectionFactory(clusterConfiguration);
	}

	@Bean(name = "CachingStringRedisTemplate")
	public RedisTemplate<String, String> cachingStringRedisTemplate(
		@Qualifier("CachingRedisConnectionFactory") RedisConnectionFactory factory) {

		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

	@Bean
	public ProxyManager<String> lettuceBasedProxyManager() {
		List<RedisURI> clusterNodes = redisProperties.getCluster().getNodes().stream()
			.map(node -> {
				String[] parts = node.split(":");
				return RedisURI.builder()
					.withHost(parts[0])
					.withPort(Integer.parseInt(parts[1]))
					.build();
			})
			.toList();

		this.redisClusterClient = RedisClusterClient.create(clusterNodes);
		this.redisClusterConnection = redisClusterClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

		return LettuceBasedProxyManager.builderFor(redisClusterConnection)
			.withExpirationStrategy(
				ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1)))
			.build();
	}

	@PreDestroy
	public void cleanUp() {
		if (redisClusterConnection != null) {
			redisClusterConnection.close();
		}
		if (redisClusterClient != null) {
			redisClusterClient.shutdown();
		}
	}
}