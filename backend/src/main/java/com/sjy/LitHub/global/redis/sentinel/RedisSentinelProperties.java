package com.sjy.LitHub.global.redis.sentinel;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Profile("prod")
@ConfigurationProperties(prefix = "custom.redis.sentinel")
public class RedisSentinelProperties {
	private String master;
	private List<String> nodes;
}