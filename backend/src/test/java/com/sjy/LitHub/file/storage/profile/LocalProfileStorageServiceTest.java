package com.sjy.LitHub.file.storage.profile;

import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.util.FileUtil;
import com.sjy.LitHub.file.util.ImageExceptionResolver;
import com.sjy.LitHub.file.util.ProfileImageUtil;
import com.sjy.LitHub.file.util.StorageImageUtil;
import com.sksamuel.scrimage.ImmutableImage;

@ExtendWith(MockitoExtension.class)
class LocalProfileStorageServiceTest {

	@InjectMocks
	private LocalProfileStorageService localProfileStorageService;

	@Mock
	private MultipartFile multipartFile;

	@Mock
	private User user;

	@Mock
	private UserGenFile genFile;

	private final String dummyPath = "/user/profile/512.webp";
	private final int size = 512;

	@Test
	@DisplayName("프로필 이미지 저장 성공")
	void saveProfileImages_success() {
		try (
			MockedStatic<ImageExceptionResolver> ignored = Mockito.mockStatic(ImageExceptionResolver.class);
			MockedStatic<StorageImageUtil> storageUtil = Mockito.mockStatic(StorageImageUtil.class);
			MockedStatic<FileUtil> fileUtil = Mockito.mockStatic(FileUtil.class)
		) {
			ImmutableImage image = ImmutableImage.create(10, 10);
			given(genFile.getFilePath()).willReturn(dummyPath);
			given(genFile.getFileNo()).willReturn(size);

			storageUtil.when(() -> StorageImageUtil.convertToImmutableWebp(any())).thenReturn(image);
			fileUtil.when(() -> FileUtil.getFileSize(anyString())).thenReturn(1234L);

			localProfileStorageService.saveProfileImages(multipartFile, List.of(genFile));

			storageUtil.verify(() -> StorageImageUtil.createResizedImage(eq(image), eq(dummyPath), eq(size)));
			fileUtil.verify(() -> FileUtil.mkdir(anyString()));
		}
	}

	@Test
	@DisplayName("프로필 이미지 삭제 성공")
	void deleteProfileImages_success() {
		// given
		long userId = 1L;
		given(user.getId()).willReturn(userId);

		try (
			MockedStatic<ProfileImageUtil> profileUtil = Mockito.mockStatic(ProfileImageUtil.class);
			MockedStatic<FileUtil> fileUtil = Mockito.mockStatic(FileUtil.class)
		) {
			profileUtil.when(() -> ProfileImageUtil.getUserProfileDir(userId))
				.thenReturn("/user/profile/");

			// when
			localProfileStorageService.deleteProfileImages(user);

			// then
			fileUtil.verify(() -> FileUtil.delete("/user/profile/"));
		}
	}
}