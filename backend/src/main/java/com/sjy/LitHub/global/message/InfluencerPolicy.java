package com.sjy.LitHub.global.message;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.repository.follow.FollowRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InfluencerPolicy {

	private final FollowRepository followRepository;

	private static final long INFLUENCER_THRESHOLD = 10000;

	public boolean isInfluencer(Long userId) {
		long followerCount = followRepository.countByFolloweeId(userId);
		return followerCount >= INFLUENCER_THRESHOLD;
	}
}