package com.sjy.LitHub.file.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.global.config.AppConfig;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageExceptionResolver {

    private static final long MAX_IMAGE_SIZE = StorageImageUtil.parseMaxSize(AppConfig.getCustomMaxImageSize());

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_UPLOAD_FAILED);
        }

        validateFileSize(file.getSize());
        validateFileName(file.getOriginalFilename());
        validateMimeTypeByTika(file);
    }

    private static void validateFileSize(long size) {
        if (size > MAX_IMAGE_SIZE) {
            throw new InvalidFileException(BaseResponseStatus.EXCEED_MAX_SIZE);
        }
    }

    private static void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_IMAGE_FORMAT);
        }
    }

    private static void validateMimeTypeByTika(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            String mimeType = AppConfig.getTika().detect(is);
            if (!mimeType.startsWith("image/")) {
                throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
        }
    }
}