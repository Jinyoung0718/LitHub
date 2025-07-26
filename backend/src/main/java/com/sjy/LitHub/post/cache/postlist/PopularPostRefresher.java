package com.sjy.LitHub.post.cache.postlist;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.post.cache.enums.CachePolicy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PopularPostRefresher {

	private static final int POPULAR_RANK_THRESHOLD = 100;
	private static final double SCORE_DECAY = 0.1;

	private final StringRedisTemplate redisTemplate;

	public PopularPostRefresher(
		@Qualifier("CachingStringRedisTemplate") StringRedisTemplate redisTemplate
	) {
		this.redisTemplate = redisTemplate;
	}

	public void refreshOnAccess(Long postId) {
		String zsetKey = CachePolicy.POPULAR_POST_ZSET.getKeyFormat();

		Long rank = redisTemplate.opsForZSet().reverseRank(zsetKey, postId.toString());
		if (rank == null) {
			log.debug("게시글 {}은 인기 ZSet에 포함되어 있지 않습니다", postId);
			return;
		}

		if (rank < POPULAR_RANK_THRESHOLD) {
			redisTemplate.opsForZSet().incrementScore(zsetKey, postId.toString(), -SCORE_DECAY);
		}
	}
}