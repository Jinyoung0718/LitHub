package com.sjy.LitHub.file.util;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    }

    public static void createResizedImage(ImmutableImage webpImage, String filePath, int width) {
        try {
            ImmutableImage resized = webpImage.scaleToWidth(width);
            resized.output(getDefaultWebpWriter(), new File(filePath));
        } catch (Exception e) {
            log.error("리사이징 이미지 생성 실패 ({}): {}", filePath, e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    public static long parseMaxSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isBlank()) return DEFAULT_SIZE;

        String trimmed = sizeStr.trim().toUpperCase(Locale.ROOT);
        Pattern pattern = Pattern.compile("(\\d+)([KMG]?B)");
        Matcher matcher = pattern.matcher(trimmed);

        if (!matcher.matches()) return DEFAULT_SIZE;

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