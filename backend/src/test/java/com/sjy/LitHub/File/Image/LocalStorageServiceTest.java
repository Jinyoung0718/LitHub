package com.sjy.LitHub.File.Image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("LocalStorageService 단위 테스트")
class LocalStorageServiceTest {

    @InjectMocks
    private LocalStorageService localStorageService;

    private String userId;

    @BeforeEach
    void setUp() throws IOException {
        userId = "testUser";
        File tempFile = File.createTempFile("profile", ".jpg");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0});
        }
        tempFile.deleteOnExit();
    }

    @Test
    @DisplayName("기본 프로필 이미지 URL 반환")
    void getDefaultProfileImageUrls_Success() {
        String[] urls = localStorageService.getDefaultProfileImageUrls();
        assertNotNull(urls);
        assertEquals(2, urls.length);
        assertEquals("http://localhost:8080/Image/baseprofile/base-profile_48.webp", urls[0]);
        assertEquals("http://localhost:8080/Image/baseprofile/base-profile_128.webp", urls[1]);
    }

    @Test
    @DisplayName("프로필 이미지 삭제 성공")
    void deleteProfileImage_Success() {
        String imageDir = "src/main/resources/static/Image/profile/" + userId;

        File dir = new File(imageDir);
        boolean isCreated = dir.mkdirs();

        assertTrue(isCreated || dir.exists());

        localStorageService.deleteImage(userId);

        assertFalse(dir.exists());
    }
}

