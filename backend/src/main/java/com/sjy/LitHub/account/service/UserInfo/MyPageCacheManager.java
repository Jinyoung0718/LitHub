package com.sjy.LitHub.account.service.UserInfo;

import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageCacheManager {

    private final CacheManager cacheManager;

    public <T> Optional<T> getCache(String key, Class<T> type) {
        Cache cache = cacheManager.getCache("myPageCache");
        if (cache == null) return Optional.empty();
        return Optional.ofNullable(cache.get(key, type));
    }

    public <T> void putCache(String key, T data) {
        Cache cache = cacheManager.getCache("myPageCache");
        if (cache != null) {
            cache.put(key, data);
        }
    }

    public void evictCache(String key) {
        Cache cache = cacheManager.getCache("myPageCache");
        if (cache != null) {
            cache.evict(key);
        }
    }
}