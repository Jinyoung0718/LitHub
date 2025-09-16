package com.sjy.LitHub.file.storage.post;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.file.ImageType;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.file.util.common.ImageExceptionUtil;
import com.sjy.LitHub.file.util.common.StorageImageUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;

import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sksamuel.scrimage.ImmutableImage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile({"dev", "test"})
public class LocalPostImageStorageService implements PostImageStorage {

	@Override
	public void savePostImage(MultipartFile file, PostGenFile postGenFile) {
		ImageExceptionUtil.validate(file);
		String filePath = FileConstant.toLocalPath(postGenFile.getStorageKey());

		try {
			if (postGenFile.getTypeCode() == PostGenFile.TypeCode.THUMBNAIL) {
				int width = ImageType.POST_THUMBNAIL.getPrimarySize();
				ImmutableImage webpImage = StorageImageUtil.convertToImmutableWebp(file);
				StorageImageUtil.createResizedImage(webpImage, filePath, width);

			} else if (postGenFile.getTypeCode() == PostGenFile.TypeCode.MARKDOWN) {
				String ext = LocalFileUtil.getFileExtension(file.getOriginalFilename()).toLowerCase();

				if (FileConstant.RAW_EXTENSIONS.contains(ext)) {
					LocalFileUtil.mkdir(Path.of(filePath).getParent().toString());
					file.transferTo(new File(filePath));

				} else {
					ImmutableImage webpImage = StorageImageUtil.convertToImmutableWebp(file);
					int maxWidth = ImageType.POST_MARKDOWN.getPrimarySize();
					int resizeWidth = Math.min(webpImage.width, maxWidth);

					StorageImageUtil.createResizedImage(webpImage, filePath, resizeWidth);
				}
			}

			String finalExt = LocalFileUtil.getFileExtension(filePath).toLowerCase();
			postGenFile.setFileExt(finalExt);

		} catch (IOException e) {
			throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
		}
	}

	@Override
	public void deletePostImage(PostGenFile postGenFile) {
		String filePath = FileConstant.toLocalPath(postGenFile.getStorageKey());
		LocalFileUtil.delete(filePath);
	}
}