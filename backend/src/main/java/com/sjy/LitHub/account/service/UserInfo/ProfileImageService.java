package com.sjy.LitHub.account.service.UserInfo;

import com.sjy.LitHub.File.Image.StorageStrategy;
import com.sjy.LitHub.File.Image.util.ImageType;
import com.sjy.LitHub.account.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class ProfileImageService {

    private final StorageStrategy storageStrategy;
    private final UserRepository userRepository;

    // 사용자가 직접 업로드한 프로필 파일을 저장
    @Transactional
    public Map<String, String> saveUserImage(MultipartFile file, String userId) {
        String imageUrl = storageStrategy.saveImage(file, userId, ImageType.PROFILE);
        return updateUserProfileImage(userId, imageUrl);
    } // 48 과 128 중에 128을 반환 받아서 프론트에서 렌더링 시, 모든 정보가 아닌 업데이트후 해당 url 로 프로필 부분만 수정

    // 기본 프로필 URL 반환
    public String[] getDefaultProfileImageUrls() {
        return storageStrategy.getDefaultProfileImageUrls();
    }

    @Transactional
    public Map<String, String> updateUserProfileImage(String userId, String imageUrl) {
        String smallImageUrl = imageUrl.replace("128.webp", "48.webp");
        userRepository.updateUserProfileImage(userId, smallImageUrl, imageUrl);

        return Map.of(
                "smallImageUrl", smallImageUrl,
                "largeImageUrl", imageUrl
        );
    } // 프로필 업데이트 후, 128과 48 모두 다 저장해버림

    @Transactional
    public void deleteUserImage(String userId) {
        storageStrategy.deleteImage(userId);
        String[] defaultImages = storageStrategy.getDefaultProfileImageUrls();
        userRepository.resetUserProfileImage(userId, defaultImages[0], defaultImages[1]);
    }
}