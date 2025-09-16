package com.sjy.LitHub.file.storage.profile;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.ImageType;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;
import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.file.util.common.ImageExceptionUtil;
import com.sjy.LitHub.file.util.prod.S3FileUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
public class S3ProfileStorageService implements ProfileImageStorage {

	private final UserGenFileMapper userGenFileMapper;
	private final S3FileUtil s3FileUtil;

	@Override
	public Map<UserGenFile.TypeCode, UserGenFile> saveProfileImagesAndReturnEntities(User user, MultipartFile originalFile) {
		ImageExceptionUtil.validate(originalFile);

		try {
			String originalFileName = originalFile.getOriginalFilename();
			String originalKey = FileConstant.s3UserKey(
				user.getId(),
				System.currentTimeMillis() + FileConstant.ORIGINAL_FILE_NAME_SEPARATOR + originalFileName
			);

			s3FileUtil.upload(originalKey, originalFile.getBytes(), originalFile.getContentType());

			return ImageType.PROFILE.getSizes().stream()
				.map(size -> {
					String resizedKey = FileConstant.s3UserKey(user.getId(), size + ".webp");
					UserGenFile entity = userGenFileMapper.toEntity(user, size, "webp");
					entity.setStorageKey(resizedKey);
					return entity;
				})
				.collect(Collectors.toMap(UserGenFile::getTypeCode, Function.identity()));

		} catch (IOException e) {
			log.error("프로필 이미지 업로드 실패: {}", e.getMessage(), e);
			throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
		}
	}

	@Override
	public void deleteProfileImages(User user) {
		for (int size : ImageType.PROFILE.getSizes()) {
			String key = FileConstant.s3UserKey(user.getId(), size + ".webp");
			try {
				s3FileUtil.delete(key);
			} catch (Exception e) {
				log.warn("프로필 이미지 삭제 실패 (userId={}, size={}): {}", user.getId(), size, e.getMessage());
			}
		}
	}
}