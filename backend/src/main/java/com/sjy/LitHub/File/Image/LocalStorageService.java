package com.sjy.LitHub.File.Image;

import com.sjy.LitHub.File.Image.util.ImageExceptionResolver;
import com.sjy.LitHub.File.Image.util.ImageType;
import com.sjy.LitHub.File.Image.util.StorageImageUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@Profile({"dev", "test"})
public class LocalStorageService implements StorageStrategy {

    @Override
    public String saveImage(MultipartFile file, String id, ImageType imageType) {
        return switch (imageType) {
            case PROFILE -> saveProfileImage(file, id);
            case POST_THUMBNAIL, POST_CONTENT -> savePostImage(); // 현재 미구현
        };
    }

    private String saveProfileImage(MultipartFile file, String id) {
        String uploadDir = "src/main/resources/static/Image/profile/" + id + "/";
        StorageImageUtil.createDirectoryIfNotExists(uploadDir);
        ImageExceptionResolver.validateImageFile(file);

        return processProfileFileUpload(file, uploadDir, id);
    }

    private String processProfileFileUpload(MultipartFile file, String uploadDir, String id) {
        try {
            String fileName = "profile.webp";
            File destinationFile = new File(uploadDir, fileName);
            file.transferTo(destinationFile);

            File webpFile = StorageImageUtil.convertToWebp(destinationFile, uploadDir);
            StorageImageUtil.createResizedImages(webpFile, uploadDir, ImageType.PROFILE);

            return "http://localhost:8080/" + StorageImageUtil.getDirectoryPath(id, ImageType.PROFILE) + "/128.webp";
        } catch (IOException e) {
            throw new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED);
        }
    }

    private String savePostImage() {
        // 추후 POST 저장 로직 구현 예정 -> webp 후 리사이징 된 URL_만 반환
        return "Post image processing not implemented yet";
    }

    @Override
    public String[] getDefaultProfileImageUrls() {
        return new String[] {
                "http://localhost:8080/Image/baseprofile/base-profile_48.webp",
                "http://localhost:8080/Image/baseprofile/base-profile_128.webp"
        };
    }

    @Override
    public void deleteImage(String userId) {
        String imageDir = "src/main/resources/static/Image/profile/" + userId;
        StorageImageUtil.deleteDirectory(imageDir);
    }
}