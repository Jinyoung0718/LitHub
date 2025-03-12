package com.sjy.LitHub.File.Image.util;

import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

@Slf4j
public class StorageImageUtil {

    public static void createDirectoryIfNotExists(String directory) {
        File dir = new File(directory);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_DIR_SAVED_FAILED);
        }
    }

    public static File convertToWebp(File originalFile, String imageDir) {
        try {
            String webpFileName = "original.webp";
            File webpFile = new File(imageDir, webpFileName);
            ImmutableImage.loader().fromFile(originalFile).output(WebpWriter.DEFAULT, webpFile);
            return webpFile;
        } catch (Exception e) {
            log.error("WebP 변환 실패: {}", e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    public static void createResizedImages(File webpFile, String imageDir, ImageType imageType) {
        try {
            ImmutableImage original = ImmutableImage.loader().fromFile(webpFile);
            for (int size : imageType.getSizes()) {
                File resizedFile = new File(imageDir, size + ".webp");
                ImmutableImage resized = original.scaleToWidth(size);
                resized.output(WebpWriter.DEFAULT, resizedFile);
            }
        } catch (Exception e) {
            log.error("리사이징 이미지 생성 실패 ({}): {}", imageType, e.getMessage(), e);
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    public static String getDirectoryPath(String id, ImageType imageType) {
        return imageType == ImageType.PROFILE ? "profile/" + id : "posts/" + id;
    }

    public static void deleteDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            log.warn("제하려는 폴더가 존재하지 않음: {}", directoryPath);
            return;
        }

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.delete()) {
                throw new InvalidFileException(BaseResponseStatus.IMAGE_DELETE_FAILED);
            }
        }

        if (!directory.delete()) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_DELETE_FAILED);
        }
    }
}