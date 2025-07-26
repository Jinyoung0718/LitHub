package com.sjy.LitHub.file.storage.post;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.ImageType;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.util.FileUtil;
import com.sjy.LitHub.file.util.StorageImageUtil;
import com.sjy.LitHub.file.util.ImageExceptionResolver;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile({"dev", "test"})
public class LocalPostImageStorageService implements PostImageStorage {

	@Override
	public void savePostImage(MultipartFile file, PostGenFile postGenFile) {
		ImageExceptionResolver.validate(file);
		String filePath = postGenFile.getFilePath();

		try {
			if (postGenFile.getTypeCode() == PostGenFile.TypeCode.THUMBNAIL) {
				int width = ImageType.POST_THUMBNAIL.getPrimarySize();
				StorageImageUtil.convertAndResizeWebp(file, filePath, width);
			} else {
				FileUtil.mkdir(Path.of(filePath).getParent().toString());
				file.transferTo(new File(filePath));
			}

			postGenFile.setFileSize(FileUtil.getFileSize(filePath));
		} catch (IOException e) {
			throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
		}
	}

	@Override
	public void deletePostImage(PostGenFile postGenFile) {
		FileUtil.delete(postGenFile.getFilePath());
	}
}