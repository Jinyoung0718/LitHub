package com.sjy.LitHub.global.security.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
public class RedisBlacklistUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisBlacklistUtil(@Qualifier("TokenStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void addToBlacklist(String token, long ttlMs) {
        String todayKey = generateBlacklistKey(LocalDate.now());
        redisTemplate.opsForSet().add(todayKey, token);
        redisTemplate.expire(todayKey, ttlMs, TimeUnit.MILLISECONDS);
    }

    public boolean isInBlackList(String token) {
        String todayKey = generateBlacklistKey(LocalDate.now());
        String yesterdayKey = generateBlacklistKey(LocalDate.now().minusDays(1));

        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(todayKey, token)) ||
            Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(yesterdayKey, token));
    }

    private String generateBlacklistKey(LocalDate date) {
        return AuthConst.TOKEN_BLACKLIST_PREFIX + date;
    }
}