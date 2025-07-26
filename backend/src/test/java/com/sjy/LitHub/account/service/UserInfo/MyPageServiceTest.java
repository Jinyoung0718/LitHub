package com.sjy.LitHub.account.service.UserInfo;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.PasswordUpdateRequestDTO;
import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.service.ReadLogService;
import com.sjy.LitHub.record.service.ReadLogStatusService;

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

	@Mock
	private MyPageCacheManager myPageCacheManager;
	@Mock private UserRepository userRepository;
	@Mock private PasswordManager passwordManager;
	@Mock private ReadLogStatusService readLogStatusService;
	@Mock private ReadLogService readLogService;

	@InjectMocks
	private MyPageService myPageService;

	private final Long userId = 1L;
	private final int year = LocalDate.now().getYear();

	@Test
	@DisplayName("캐시가 존재하면 DB 조회 없이 캐시에서 반환")
	void getCachedMyPageData_cached() {
		UserProfileResponseDTO profile = mock(UserProfileResponseDTO.class);
		ReadingStatsResponseDTO stats = mock(ReadingStatsResponseDTO.class);

		given(myPageCacheManager.getCache("userProfile:" + userId, UserProfileResponseDTO.class)).willReturn(Optional.of(profile));
		given(myPageCacheManager.getCache("readingStats:" + userId + ":" + year, ReadingStatsResponseDTO.class)).willReturn(Optional.of(stats));

		MyPageResponseDTO result = myPageService.getCachedMyPageData(userId, year);

		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("캐시가 없으면 DB 에서 조회 후 캐시에 저장")
	void getCachedMyPageData_notCached() {
		UserProfileResponseDTO profile = mock(UserProfileResponseDTO.class);
		ReadingStatsResponseDTO stats = mock(ReadingStatsResponseDTO.class);

		given(myPageCacheManager.getCache(any(), eq(UserProfileResponseDTO.class))).willReturn(Optional.empty());
		given(userRepository.getUserProfile(userId)).willReturn(profile);

		given(myPageCacheManager.getCache(any(), eq(ReadingStatsResponseDTO.class))).willReturn(Optional.empty());
		given(readLogStatusService.getReadingStats(userId, year)).willReturn(stats);

		MyPageResponseDTO result = myPageService.getCachedMyPageData(userId, year);

		verify(myPageCacheManager, times(2)).putCache(any(), any());
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("닉네임 변경 - 중복일 경우 예외 발생")
	void updateNickName_duplicate() {
		NicknameRequestDTO dto = new NicknameRequestDTO("newNick");

		given(userRepository.updateNickNameIfNotExists(userId, dto.getNickName())).willReturn(false);

		assertThatThrownBy(() -> myPageService.updateNickName(userId, dto))
			.isInstanceOf(InvalidUserException.class);
	}

	@Test
	@DisplayName("닉네임 변경 성공 시 캐시 갱신")
	void updateNickName_success() {
		NicknameRequestDTO dto = new NicknameRequestDTO("newNick");
		UserProfileResponseDTO updated = mock(UserProfileResponseDTO.class);

		given(userRepository.updateNickNameIfNotExists(userId, dto.getNickName())).willReturn(true);
		given(userRepository.getUserProfile(userId)).willReturn(updated);

		myPageService.updateNickName(userId, dto);

		verify(myPageCacheManager).putCache(eq("userProfile:" + userId), eq(updated));
	}

	@Test
	@DisplayName("비밀번호 변경 시 검증 및 캐시 삭제")
	void updatePassword_success() {
		PasswordUpdateRequestDTO dto = new PasswordUpdateRequestDTO("curr", "new");

		myPageService.updatePassword(userId, dto);

		verify(passwordManager).validatePassword(userId, "curr", "new");
		verify(passwordManager).updatePassword(userId, "new");
		verify(myPageCacheManager).evictCache("userProfile:" + userId);
	}

	@Test
	@DisplayName("회원 탈퇴 시 사용자 삭제 및 캐시 삭제")
	void deleteUser_success() {
		myPageService.deleteUser(userId);

		verify(userRepository).deleteUserById(eq(userId), any());
		verify(myPageCacheManager).evictCache("userProfile:" + userId);
		verify(myPageCacheManager).evictCache(startsWith("readingStats:" + userId));
	}

	@Test
	@DisplayName("독서 기록 저장 후 캐시 갱신")
	void saveReadingRecordAndUpdateCache_success() {
		ReadingStatsResponseDTO stats = mock(ReadingStatsResponseDTO.class);
		UserProfileResponseDTO profile = mock(UserProfileResponseDTO.class);

		given(readLogStatusService.getReadingStats(userId, year)).willReturn(stats);
		given(userRepository.getUserProfile(userId)).willReturn(profile);

		MyPageResponseDTO result = myPageService.saveReadingRecordAndUpdateCache(userId, 30);

		verify(readLogService).saveReadingRecord(userId, 30);
		verify(myPageCacheManager, times(2)).putCache(any(), any());
		assertThat(result).isNotNull();
	}
}