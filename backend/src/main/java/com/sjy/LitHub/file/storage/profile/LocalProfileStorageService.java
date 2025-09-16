package com.sjy.LitHub.file.storage.profile;

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
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.file.util.common.ImageExceptionUtil;
import com.sjy.LitHub.file.util.common.StorageImageUtil;
import com.sksamuel.scrimage.ImmutableImage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class LocalProfileStorageService implements ProfileImageStorage {

	private final UserGenFileMapper userGenFileMapper;

	@Override
	public Map<UserGenFile.TypeCode, UserGenFile> saveProfileImagesAndReturnEntities(User user, MultipartFile originalFile) {
		ImageExceptionUtil.validate(originalFile);
		ImmutableImage webpImage = StorageImageUtil.convertToImmutableWebp(originalFile);

		return ImageType.PROFILE.getSizes().stream()
			.map(size -> {
				String filePath = FileConstant.localProfileImagePath(user.getId(), size);
				StorageImageUtil.createResizedImage(webpImage, filePath, size);
				String fileExt = LocalFileUtil.getFileExtension(filePath);
				return userGenFileMapper.toEntity(user, size, fileExt);
			})
			.collect(Collectors.toMap(UserGenFile::getTypeCode, Function.identity()));
	}

	@Override
	public void deleteProfileImages(User user) {
		String userDir = FileConstant.toLocalPath(FileConstant.USER_DIR_PREFIX + "/" + user.getId());
		LocalFileUtil.delete(userDir);
	}
}