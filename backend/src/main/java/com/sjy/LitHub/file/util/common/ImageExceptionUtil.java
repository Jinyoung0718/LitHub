package com.sjy.LitHub.file.util.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.global.config.AppConfig;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sksamuel.scrimage.ImmutableImage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageExceptionUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "svg");

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_UPLOAD_FAILED);
        }

        validateFileSize(file.getSize());
        validateFileName(file.getOriginalFilename());
        validateMimeType(file);
        validateDecoding(file);
    }

    private static void validateFileSize(long size) {
        long maxImageSize = StorageImageUtil.parseMaxSize(AppConfig.getCustomMaxImageSize());
        if (size > maxImageSize) {
            throw new InvalidFileException(BaseResponseStatus.EXCEED_MAX_SIZE);
        }
    }


    private static void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_IMAGE_FORMAT);
        }
        // 경로 조작 방지
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_FORMAT);
        }

        // 화이트리스트 확장자 검사
        String ext = LocalFileUtil.getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_EXTENSION);
        }
    }

    private static void validateMimeType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            String mimeType = AppConfig.getTika().detect(is);

            switch (mimeType.split("/")[0]) {
                case "image" -> {}
                case "video" -> throw new InvalidFileException(BaseResponseStatus.UNSUPPORTED_VIDEO_UPLOAD);
                case "audio" -> throw new InvalidFileException(BaseResponseStatus.UNSUPPORTED_AUDIO_UPLOAD);
                default -> throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
            }

        } catch (IOException e) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE);
        }
    }

    private static void validateDecoding(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            // 실제 디코딩 시도 (깨지거나 가짜면 여기서 실패)
            ImmutableImage.loader().fromStream(is);
        } catch (Exception e) {
            log.error("이미지 디코딩 실패 (악성 또는 손상된 파일 가능): {}", file.getOriginalFilename(), e);
            throw new InvalidFileException(BaseResponseStatus.INVALID_IMAGE_FORMAT);
        }
    }
}