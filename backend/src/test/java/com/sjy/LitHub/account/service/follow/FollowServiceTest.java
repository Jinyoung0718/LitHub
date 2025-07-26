package com.sjy.LitHub.account.service.follow;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sjy.LitHub.account.entity.Follow;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.model.res.FollowListResponseDTO;
import com.sjy.LitHub.account.repository.follow.FollowRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.PageResponse;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

	@InjectMocks
	private FollowService followService;

	@Mock
	private FollowRepository followRepository;

	@Mock
	private UserRepository userRepository;

	private final Long followerId = 1L;
	private final Long followeeId = 2L;

	@Test
	@DisplayName("팔로우 토글 - 이미 팔로우 중이면 삭제")
	void toggleFollow_alreadyFollowing_deletesFollow() {
		given(followRepository.existsByFollowerAndFollowee(followerId, followeeId)).willReturn(true);
		given(followRepository.deleteByFollowerAndFollowee(followerId, followeeId)).willReturn(1);

		followService.toggleFollow(followerId, followeeId);

		then(followRepository).should().deleteByFollowerAndFollowee(followerId, followeeId);
	}

	@Test
	@DisplayName("팔로우 토글 - 팔로우 중이 아니면 팔로우 생성")
	void toggleFollow_notFollowing_savesFollow() {
		User follower = mock(User.class);
		User followee = mock(User.class);

		given(followRepository.existsByFollowerAndFollowee(followerId, followeeId)).willReturn(false);
		given(userRepository.getReferenceById(followerId)).willReturn(follower);
		given(userRepository.getReferenceById(followeeId)).willReturn(followee);

		followService.toggleFollow(followerId, followeeId);

		then(followRepository).should().save(any(Follow.class));
	}

	@Test
	@DisplayName("팔로우 토글 - 자기 자신을 팔로우하려는 경우 예외 발생")
	void toggleFollow_selfFollow_throwsException() {
		assertThatThrownBy(() -> followService.toggleFollow(followerId, followerId))
			.isInstanceOf(InvalidUserException.class);
	}

	@Test
	@DisplayName("팔로잉 목록 조회")
	void getFollowings_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<FollowListResponseDTO> page = new PageImpl<>(List.of());
		given(followRepository.findFollowingsByUserId(followerId, pageable)).willReturn(page);

		PageResponse<FollowListResponseDTO> result = followService.getFollowings(followerId, pageable);

		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("팔로워 목록 조회")
	void getFollowers_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<FollowListResponseDTO> page = new PageImpl<>(List.of());
		given(followRepository.findFollowersByUserId(followeeId, pageable)).willReturn(page);

		PageResponse<FollowListResponseDTO> result = followService.getFollowers(followeeId, pageable);

		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("팔로워 삭제 - 성공")
	void removeFollower_success() {
		given(followRepository.deleteByFollowerAndFollowee(followerId, followeeId)).willReturn(1);

		followService.removeFollower(followeeId, followerId);

		then(followRepository).should().deleteByFollowerAndFollowee(followerId, followeeId);
	}

	@Test
	@DisplayName("팔로워 삭제 - 실패 시 예외 발생")
	void removeFollower_notFound_throwsException() {
		given(followRepository.deleteByFollowerAndFollowee(followerId, followeeId)).willReturn(0);

		assertThatThrownBy(() -> followService.removeFollower(followeeId, followerId))
			.isInstanceOf(InvalidUserException.class);
	}
}