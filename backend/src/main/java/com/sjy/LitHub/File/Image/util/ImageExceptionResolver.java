package com.sjy.LitHub.File.Image.util;

import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class ImageExceptionResolver {

    private static final long MAX_IMG_FILE_SIZE = 10 * 1024 * 1024L;

    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_UPLOAD_FAILED);
        }

        validateFileSize(file.getSize());
        validateMimeType(file.getContentType());
        validateFileName(file.getOriginalFilename());
    }

    public static void validateFileSize(long fileSize) {
        if (fileSize > MAX_IMG_FILE_SIZE) {
            throw new InvalidFileException(BaseResponseStatus.EXCEED_MAX_SIZE);
        }
    }

    public static void validateMimeType(String mimeType) {
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
        }
    }

    public static void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_IMAGE_FORMAT);
        }
    }
}