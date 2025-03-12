package com.sjy.LitHub.account.service.UserInfo;

import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class MyPageCacheManager {

    private final CacheManager cacheManager;

    public MyPageResponseDTO getCachedMyPageData(Long userId, Supplier<MyPageResponseDTO> fetchDataFunction) {
        return getCache(userId)
                .orElseGet(() -> fetchAndCache(userId, fetchDataFunction));
    }

    public void evictCache(Long userId) {
        Cache cache = cacheManager.getCache("myPageCache");
        if (cache != null) {
            cache.evict(userId);
        }
    }

    private Optional<MyPageResponseDTO> getCache(Long userId) {
        Cache cache = cacheManager.getCache("myPageCache");
        if (cache == null) return Optional.empty();
        return Optional.ofNullable(cache.get(userId, MyPageResponseDTO.class));
    }

    private MyPageResponseDTO fetchAndCache(Long userId, Supplier<MyPageResponseDTO> fetchDataFunction) {
        MyPageResponseDTO response = fetchDataFunction.get();
        Cache cache = cacheManager.getCache("myPageCache");
        if (cache != null) {
            cache.put(userId, response);
        }
        return response;
    }
}