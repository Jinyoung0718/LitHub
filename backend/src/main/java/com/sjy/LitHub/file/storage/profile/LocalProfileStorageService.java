package com.sjy.LitHub.file.storage.profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.util.FileUtil;
import com.sjy.LitHub.file.util.ProfileImageUtil;
import com.sjy.LitHub.file.util.StorageImageUtil;
import com.sjy.LitHub.file.util.exception.ImageExceptionResolver;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sksamuel.scrimage.ImmutableImage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class LocalProfileStorageService implements ProfileImageStorage {

	@Override
	public void saveProfileImages(MultipartFile originalFile, Collection<UserGenFile> genFiles) {
		ImageExceptionResolver.validate(originalFile);
		File tempFile = createTempFile(originalFile);
		ImmutableImage webpImage = StorageImageUtil.convertToImmutableWebp(tempFile);

		for (UserGenFile genFile : genFiles) {
			String resizedFilePath = genFile.getFilePath();
			String directoryPath = Path.of(resizedFilePath).getParent().toString();
			FileUtil.mkdir(directoryPath);

			StorageImageUtil.createResizedImage(webpImage, resizedFilePath, genFile.getFileNo());
			genFile.setFileSize(FileUtil.getFileSize(resizedFilePath));
		}

		if (!tempFile.delete()) {
			log.warn("임시 파일 삭제 실패: {}", tempFile.getAbsolutePath());
		}
	}

	@Override
	public void deleteProfileImages(User user) {
		String profileDir = ProfileImageUtil.getUserProfileDir(user.getId());
		FileUtil.delete(profileDir);
	}

	private File createTempFile(MultipartFile file) {
		try {
			File temp = File.createTempFile("upload_", ".tmp");
			file.transferTo(temp);
			return temp;
		} catch (IOException e) {
			throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
		}
	}
}