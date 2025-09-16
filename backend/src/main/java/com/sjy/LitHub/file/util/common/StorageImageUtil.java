package com.sjy.LitHub.file.util.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageImageUtil {

    public static ImmutableImage convertToImmutableWebp(InputStream is) {
        try {
            WebpWriter webpWriter = getDefaultWebpWriter();
            byte[] webpBytes = ImmutableImage.loader()
                .fromStream(is)
                .bytes(webpWriter);

            return ImmutableImage.loader().fromBytes(webpBytes);
        } catch (Exception e) {
            log.error("WebP 변환 실패: {}", e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    public static ImmutableImage convertToImmutableWebp(MultipartFile multipartFile) {
        try (InputStream is = multipartFile.getInputStream()) {
            return convertToImmutableWebp(is);
        } catch (IOException e) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    public static void createResizedImage(ImmutableImage webpImage, String filePath, int width) {
        try {
            ImmutableImage resized = webpImage.scaleToWidth(width);
            LocalFileUtil.mkdir(Path.of(filePath).getParent().toString());
            resized.output(getDefaultWebpWriter(), new File(filePath));
        } catch (Exception e) {
            log.error("리사이징 이미지 생성 실패 ({}): {}", filePath, e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    public static long parseMaxSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isBlank()) {
            return FileConstant.DEFAULT_SIZE;
        }
        String trimmed = sizeStr.trim().toUpperCase(Locale.ROOT);
        Pattern pattern = Pattern.compile("(\\d+)([KMG]?B)");
        Matcher matcher = pattern.matcher(trimmed);
        if (!matcher.matches()) return FileConstant.DEFAULT_SIZE;

        long size = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "KB" -> size * 1024L;
            case "MB" -> size * 1024L * 1024L;
            case "GB" -> size * 1024L * 1024L * 1024L;
            default -> size;
        };
    }

    public static WebpWriter getDefaultWebpWriter() {
        return new WebpWriter().withQ(95).withZ(6).withM(6);
    }
}