package com.sjy.LitHub.global.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class RedisConfig {

    // 토큰 전용 레디스
    private final RedisProperties redisProperties;

    // 캐싱 전용 레디스
    @Value("${custom.redis.cache.host}")
    private String cacheRedisHost;

    @Value("${custom.redis.cache.port}")
    private int cacheRedisPort;

    private RedisClient redisClient;
    private StatefulRedisConnection<String, byte[]> redisConnection;

    @Bean(name = "TokenRedisConnectionFactory")
    public RedisConnectionFactory tokenRedisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean(name = "CacheRedisConnectionFactory")
    public RedisConnectionFactory cacheRedisConnectionFactory() {
        return new LettuceConnectionFactory(cacheRedisHost, cacheRedisPort);
    }

    // 캐시용 RedisTemplate (String-String)
    @Bean(name = "CachingStringRedisTemplate")
    public RedisTemplate<String, String> cachingStringRedisTemplate(
        @Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory factory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 토큰용 RedisTemplate (String-String)
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

    // 임시 토큰용 RedisTemplate (String-JSON)
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

    // 외부 라이브러리 또는 기본 주입용
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> defaultRedisTemplate(
        @Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory factory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public ProxyManager<String> lettuceBasedProxyManager() {
        this.redisClient = RedisClient.create(RedisURI.builder()
            .withHost(cacheRedisHost)
            .withPort(cacheRedisPort)
            .build());

        this.redisConnection = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return LettuceBasedProxyManager.builderFor(redisConnection)
            .withExpirationStrategy(
                ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1L)))
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