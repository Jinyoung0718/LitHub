package com.sjy.LitHub.file.util.exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.util.StorageImageUtil;
import com.sjy.LitHub.global.config.AppConfig;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileExceptionResolver {

	private static final long MAX_FILE_SIZE = StorageImageUtil.parseMaxSize(AppConfig.getCustomMaxFileSize());

	public static void validate(MultipartFile file, List<String> allowedMimePrefixes) {
		if (file == null || file.isEmpty()) {
			throw new InvalidFileException(BaseResponseStatus.FILE_UPLOAD_FAILED);
		}

		validateFileSize(file.getSize());
		validateFileName(file.getOriginalFilename());
		validateMimeType(file, allowedMimePrefixes);
	}

	private static void validateFileSize(long size) {
		if (size > MAX_FILE_SIZE) {
			throw new InvalidFileException(BaseResponseStatus.EXCEED_MAX_SIZE);
		}
	}

	private static void validateFileName(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_FORMAT);
		}
	}

	private static void validateMimeType(MultipartFile file, List<String> allowedMimePrefixes) {
		try (InputStream is = file.getInputStream()) {
			String mimeType = AppConfig.getTika().detect(is);
			boolean matched = allowedMimePrefixes.stream().anyMatch(mimeType::startsWith);
			if (!matched) {
				throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
			}
		} catch (IOException e) {
			throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
		}
	}
}