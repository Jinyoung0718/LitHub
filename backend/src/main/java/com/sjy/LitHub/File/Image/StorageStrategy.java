package com.sjy.LitHub.File.Image;

import com.sjy.LitHub.File.Image.util.ImageType;
import org.springframework.web.multipart.MultipartFile;

public interface StorageStrategy {
    String saveImage(MultipartFile file, String identifier, ImageType imageType);
    String[] getDefaultProfileImageUrls();
    void deleteImage(String identifier);
}