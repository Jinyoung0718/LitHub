package com.sjy.LitHub.file.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.service.UserInfo.MyPageCacheManager;
import com.sjy.LitHub.account.util.UserCacheKeyConstants;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;
import com.sjy.LitHub.file.storage.profile.ProfileImageStorage;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

	private final UserRepository userRepository;
	private final ProfileImageStorage profileImageStorage;
	private final MyPageCacheManager myPageCacheManager;
	private final UserGenFileMapper userGenFileMapper;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	@Transactional
	public String saveUserImage(MultipartFile file, Long userId) {
		User user = userRepository.findUserWithGenFilesById(userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));

		Map<UserGenFile.TypeCode, UserGenFile> newFiles =
			profileImageStorage.saveProfileImagesAndReturnEntities(user, file);

		applyProfileUpdate(user, newFiles);
		return user.getProfileImageUrl512();
	}

	@Transactional
	public String deleteUserImage(Long userId) {
		User user = userRepository.findUserWithGenFilesById(userId)
			.orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));

		profileImageStorage.deleteProfileImages(user);
		Map<UserGenFile.TypeCode, UserGenFile> defaultFiles = userGenFileMapper.toDefaultUserGenFiles(user);
		applyProfileUpdate(user, defaultFiles);
		return user.getProfileImageUrl512();
	}

	private void applyProfileUpdate(User user, Map<UserGenFile.TypeCode, UserGenFile> newFiles) {
		user.replaceUserGenFiles(newFiles);

		afterCommitExecutor.executeAfterCommit(() ->
			myPageCacheManager.putCache(
				UserCacheKeyConstants.profileKey(user.getId()),
				userRepository.getUserProfile(user.getId())
			)
		);
	}
}