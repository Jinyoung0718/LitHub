package com.sjy.LitHub.account.service.follow;

import com.sjy.LitHub.account.entity.Follow;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.model.res.FollowListResponseDTO;
import com.sjy.LitHub.account.repository.follow.FollowRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	@Transactional
	public void toggleFollow(Long followerId, Long followeeId) {
		if (followerId.equals(followeeId)) {
			throw new InvalidUserException(BaseResponseStatus.FOLLOW_SELF_NOT_ALLOWED);
		}

		boolean exists = followRepository.existsByFollowerAndFollowee(followerId, followeeId);

		if (exists) {
			int deleted = followRepository.deleteByFollowerAndFollowee(followerId, followeeId);
			if (deleted == 0) {
				throw new InvalidUserException(BaseResponseStatus.FOLLOW_DELETE_FAILED);
			}
		} else {
			User follower = userRepository.getReferenceById(followerId);
			User followee = userRepository.getReferenceById(followeeId);
			Follow follow = Follow.of(follower, followee);
			followRepository.save(follow);
		}
	}

	@Transactional(readOnly = true)
	public Page<FollowListResponseDTO> getFollowings(Long userId, Pageable pageable) {
		return followRepository.findFollowingsByUserId(userId, pageable);
	}

	@Transactional(readOnly = true)
	public Page<FollowListResponseDTO> getFollowers(Long userId, Pageable pageable) {
		return followRepository.findFollowersByUserId(userId, pageable);
	}

	@Transactional
	public void removeFollower(Long meAsFolloweeId, Long targetFollowerId) {
		int deleted = followRepository.deleteByFollowerAndFollowee(targetFollowerId, meAsFolloweeId);
		if (deleted == 0) {
			throw new InvalidUserException(BaseResponseStatus.FOLLOW_NOT_FOUND);
		}
	}
}