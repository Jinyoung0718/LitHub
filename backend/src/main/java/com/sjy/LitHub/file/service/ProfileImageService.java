package com.sjy.LitHub.file.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.service.UserInfo.MyPageCacheManager;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;
import com.sjy.LitHub.file.storage.profile.ProfileImageStorage;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

	private final UserRepository userRepository;
	private final ProfileImageStorage profileImageStorage;
	private final MyPageCacheManager myPageCacheManager;
	private final UserGenFileMapper userGenFileMapper;

	private static final String PROFILE_KEY_PREFIX = "userProfile:";

	@Transactional
	public String saveUserImage(MultipartFile file, Long userId) {
		User user = userRepository.findUserWithGenFilesById(userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));

		profileImageStorage.deleteProfileImages(user);
		Map<UserGenFile.TypeCode, UserGenFile> newFiles = userGenFileMapper.toUserGenFiles(user, file);
		profileImageStorage.saveProfileImages(file, newFiles.values());
		user.getUserGenFiles().clear();
		user.getUserGenFiles().putAll(newFiles);

		myPageCacheManager.evictCache(PROFILE_KEY_PREFIX + user.getId());
		return user.getProfileImageUrl512();
	}

	@Transactional
	public String deleteUserImage(Long userId) {
		User user = userRepository.findUserWithGenFilesById(userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));

		profileImageStorage.deleteProfileImages(user);
		Map<UserGenFile.TypeCode, UserGenFile> defaultFiles = userGenFileMapper.toDefaultUserGenFiles(user);
		user.getUserGenFiles().clear();
		user.getUserGenFiles().putAll(defaultFiles);

		myPageCacheManager.evictCache(PROFILE_KEY_PREFIX + user.getId());
		return user.getProfileImageUrl512();
	}
}