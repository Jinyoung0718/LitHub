package com.sjy.LitHub.file.storage.post;

import java.io.IOException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.file.util.common.ImageExceptionUtil;
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.file.util.prod.S3FileUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
public class S3PostImageStorageService implements PostImageStorage {

	private final S3FileUtil s3FileUtil;

	@Override
	public void savePostImage(MultipartFile file, PostGenFile postGenFile) {
		ImageExceptionUtil.validate(file);
		String key = postGenFile.getStorageKey();

		try {
			String ext = LocalFileUtil.getFileExtension(file.getOriginalFilename()).toLowerCase();
			s3FileUtil.upload(key, file.getBytes(), file.getContentType());
			String finalExt = FileConstant.RAW_EXTENSIONS.contains(ext) ? ext : "webp";
			postGenFile.setFileExt(finalExt);
		} catch (IOException e) {
			log.error("포스트 이미지 업로드 실패: {}", e.getMessage(), e);
			throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
		}
	}

	@Override
	public void deletePostImage(PostGenFile postGenFile) {
		try {
			s3FileUtil.delete(postGenFile.getStorageKey());
		} catch (Exception e) {
			log.warn("포스트 이미지 삭제 실패 (postId={}, key={}): {}",
				postGenFile.getPost().getId(), postGenFile.getStorageKey(), e.getMessage());
		}
	}
}