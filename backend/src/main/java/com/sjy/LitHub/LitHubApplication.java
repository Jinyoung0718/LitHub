package com.sjy.LitHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sjy.LitHub.global.redis.sentinel.RedisSentinelProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(RedisSentinelProperties.class)
public class LitHubApplication {
	public static void main(String[] args) {
		SpringApplication.run(LitHubApplication.class, args);
	}
}