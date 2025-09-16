package com.sjy.LitHub.account.repository.follow;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sjy.LitHub.account.model.res.FollowListResponseDTO;

public interface FollowRepositoryCustom {

	boolean existsByFollowerAndFollowee(Long followerId, Long followeeId);

	Page<FollowListResponseDTO> findFollowingsByUserId(Long userId, Pageable pageable);

	Page<FollowListResponseDTO> findFollowersByUserId(Long userId, Pageable pageable);

	int deleteByFollowerAndFollowee(Long followerId, Long followeeId);

	Map<Long, Long> countFollowersByFolloweeIds(Collection<Long> followeeIds);
}