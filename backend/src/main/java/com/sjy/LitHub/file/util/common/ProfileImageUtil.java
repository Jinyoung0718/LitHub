package com.sjy.LitHub.file.util.common;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.file.util.prod.S3FileUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileImageUtil {

	private final S3FileUtil s3FileUtil;

	// S3 환경: 기본 프로필 복사
	public String copyBaseProfileOnS3(Long userId, int size) {
		String sourceKey = FileConstant.s3BaseProfileKey(size);
		String ext = LocalFileUtil.getFileExtension(sourceKey);
		String targetKey = FileConstant.s3UserKey(userId, size + "." + ext);
		s3FileUtil.copy(sourceKey, targetKey);
		return targetKey;
	}

	// 로컬 환경: 기본 프로필 복사
	public static String copyBaseProfileLocal(Long userId, int size) {
		Path source = Path.of(FileConstant.localBaseProfilePath(size));
		Path target = Path.of(FileConstant.localProfileImagePath(userId, size));
		LocalFileUtil.copy(source.toString(), target.toString());
		return target.toString();
	}
}
