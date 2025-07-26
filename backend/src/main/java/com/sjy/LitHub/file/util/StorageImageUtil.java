package com.sjy.LitHub.file.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageImageUtil {

    private static final long DEFAULT_SIZE = 10 * 1024 * 1024L;

    public static ImmutableImage convertToImmutableWebp(File originalFile) {
        try {
            WebpWriter webpWriter = getDefaultWebpWriter();
            byte[] webpBytes = ImmutableImage.loader()
                .fromFile(originalFile)
                .bytes(webpWriter);

            return ImmutableImage.loader().fromBytes(webpBytes);
        } catch (Exception e) {
            log.error("WebP 변환 실패: {}", e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    } // 원본 파일을 WebP 포맷의 ImmutableImage 객체로 변환한다.

    public static void convertAndResizeWebp(MultipartFile multipartFile, String filePath, int width) {
        File tempFile = createTempFile(multipartFile);
        try {
            ImmutableImage webpImage = convertToImmutableWebp(tempFile);
            FileUtil.mkdir(Path.of(filePath).getParent().toString());
            createResizedImage(webpImage, filePath, width);
        } finally {
            if (!tempFile.delete()) {
                log.warn("임시 파일 삭제 실패: {}", tempFile.getAbsolutePath());
            }
        }
    } // MultipartFile 을 WebP로 변환하고 지정된 크기로 리사이징하여 저장한다.

    public static void createResizedImage(ImmutableImage webpImage, String filePath, int width) {
        try {
            ImmutableImage resized = webpImage.scaleToWidth(width);
            resized.output(getDefaultWebpWriter(), new File(filePath));
        } catch (Exception e) {
            log.error("리사이징 이미지 생성 실패 ({}): {}", filePath, e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    } // 리사이징된 WebP 이미지를 지정된 파일 경로에 저장한다.[덮어쓰기 됨]

    private static File createTempFile(MultipartFile file) {
        try {
            File temp = File.createTempFile("upload_", ".tmp");
            file.transferTo(temp);
            return temp;
        } catch (IOException e) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    } // MultipartFile 을 임시 파일로 저장한다.

    public static long parseMaxSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isBlank()) {
            return DEFAULT_SIZE;
        }

        String trimmed = sizeStr.trim().toUpperCase(Locale.ROOT);
        Pattern pattern = Pattern.compile("(\\d+)([KMG]?B)");
        Matcher matcher = pattern.matcher(trimmed);

        if (!matcher.matches()) {
            return DEFAULT_SIZE;
        }

        long size = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "KB" -> size * 1024L;
            case "MB" -> size * 1024L * 1024L;
            case "GB" -> size * 1024L * 1024L * 1024L;
            default -> size;
        };
    }

    private static WebpWriter getDefaultWebpWriter() {
        return new WebpWriter().withQ(95).withZ(6).withM(6);
    }
}