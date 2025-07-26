package com.sjy.LitHub.file.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.service.UserInfo.MyPageCacheManager;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;
import com.sjy.LitHub.file.storage.profile.ProfileImageStorage;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProfileImageServiceTest {

	@InjectMocks
	private ProfileImageService profileImageService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProfileImageStorage profileImageStorage;

	@Mock
	private MyPageCacheManager myPageCacheManager;

	@Mock
	private UserGenFileMapper userGenFileMapper;

	private final Long userId = 1L;
	private User mockUser;
	private MultipartFile file;

	@BeforeEach
	void setup() {
		mockUser = User.builder().id(userId).userGenFiles(new EnumMap<>(UserGenFile.TypeCode.class)).build();
		file = new MockMultipartFile("file", "test.png", "image/png", "fake-content".getBytes());
	}

	@Test
	@DisplayName("유저 프로필 이미지 저장 성공")
	void saveUserImage_success() {
		// given
		UserGenFile file512 = UserGenFile.builder()
			.user(mockUser)
			.fileName("512.webp")
			.fileNo(512)
			.build();

		Map<UserGenFile.TypeCode, UserGenFile> newFiles = Map.of(
			UserGenFile.TypeCode.PROFILE_512, file512
		);


		given(userRepository.findUserWithGenFilesById(userId)).willReturn(Optional.of(mockUser));
		given(userGenFileMapper.toUserGenFiles(mockUser, file)).willReturn(newFiles);

		// when
		String imageUrl = profileImageService.saveUserImage(file, userId);

		// then
		then(profileImageStorage).should().deleteProfileImages(mockUser);
		then(profileImageStorage).should().saveProfileImages(eq(file), eq(newFiles.values()));
		then(myPageCacheManager).should().evictCache("userProfile:" + userId);
		assertThat(imageUrl).isEqualTo(mockUser.getProfileImageUrl512());
	}

	@Test
	@DisplayName("유저 프로필 이미지 삭제 성공")
	void deleteUserImage_success() {
		// given
		Map<UserGenFile.TypeCode, UserGenFile> defaultFiles = Map.of(
			UserGenFile.TypeCode.PROFILE_512,
			UserGenFile.builder()
				.user(mockUser)
				.fileName("default.webp")
				.fileNo(512)
				.build()
		);

		given(userRepository.findUserWithGenFilesById(userId)).willReturn(Optional.of(mockUser));
		given(userGenFileMapper.toDefaultUserGenFiles(mockUser)).willReturn(defaultFiles);

		// when
		String imageUrl = profileImageService.deleteUserImage(userId);

		// then
		then(profileImageStorage).should().deleteProfileImages(mockUser);
		then(myPageCacheManager).should().evictCache("userProfile:" + userId);
		assertThat(imageUrl).isEqualTo(mockUser.getProfileImageUrl512());
	}

	@Test
	@DisplayName("유저 프로필 이미지 저장 실패 - 유저 없음")
	void saveUserImage_userNotFound() {
		given(userRepository.findUserWithGenFilesById(userId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> profileImageService.saveUserImage(file, userId))
			.isInstanceOf(InvalidUserException.class);
	}

	@Test
	@DisplayName("유저 프로필 이미지 삭제 실패 - 유저 없음")
	void deleteUserImage_userNotFound() {
		given(userRepository.findUserWithGenFilesById(userId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> profileImageService.deleteUserImage(userId))
			.isInstanceOf(InvalidUserException.class);
	}
}