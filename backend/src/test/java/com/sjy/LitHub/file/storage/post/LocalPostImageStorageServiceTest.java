package com.sjy.LitHub.file.storage.post;

import static org.aspectj.bridge.MessageUtil.*;
import static org.mockito.BDDMockito.*;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.util.FileUtil;
import com.sjy.LitHub.file.util.ImageExceptionResolver;
import com.sjy.LitHub.file.util.StorageImageUtil;

@ExtendWith(MockitoExtension.class)
class LocalPostImageStorageServiceTest {

	@InjectMocks
	private LocalPostImageStorageService postImageStorageService;

	@Mock
	private MultipartFile file;

	@Mock
	private PostGenFile postGenFile;

	private final String dummyPath = "/dummy/path/image.webp";

	@Test
	@DisplayName("썸네일 이미지 저장 성공")
	void savePostImage_thumbnail_success() {
		given(postGenFile.getFilePath()).willReturn(dummyPath);
		given(postGenFile.getTypeCode()).willReturn(PostGenFile.TypeCode.THUMBNAIL);

		try (
			MockedStatic<ImageExceptionResolver> resolverMock = mockStatic(ImageExceptionResolver.class);
			MockedStatic<StorageImageUtil> imageUtil = mockStatic(StorageImageUtil.class);
			MockedStatic<FileUtil> fileUtil = mockStatic(FileUtil.class)
		) {
			fileUtil.when(() -> FileUtil.getFileSize(dummyPath)).thenReturn(1234L);

			postImageStorageService.savePostImage(file, postGenFile);

			imageUtil.verify(() -> StorageImageUtil.convertAndResizeWebp(eq(file), eq(dummyPath), anyInt()));
			fileUtil.verify(() -> FileUtil.getFileSize(dummyPath));
			then(postGenFile).should().setFileSize(1234L);
		}
	}

	@Test
	@DisplayName("일반 마크다운 이미지 저장 성공")
	void savePostImage_markdown_success() {
		given(postGenFile.getFilePath()).willReturn(dummyPath);
		given(postGenFile.getTypeCode()).willReturn(PostGenFile.TypeCode.MARKDOWN);

		try (
			MockedStatic<ImageExceptionResolver> resolverMock = mockStatic(ImageExceptionResolver.class);
			MockedStatic<FileUtil> fileUtil = mockStatic(FileUtil.class)
		) {
			fileUtil.when(() -> FileUtil.getFileSize(dummyPath)).thenReturn(1234L);

			postImageStorageService.savePostImage(file, postGenFile);

			fileUtil.verify(() -> FileUtil.mkdir(Path.of(dummyPath).getParent().toString()));
			fileUtil.verify(() -> FileUtil.getFileSize(dummyPath));
			then(postGenFile).should().setFileSize(1234L);
		}
	}


	@Test
	@DisplayName("포스트 이미지 삭제 성공")
	void deletePostImage_success() {
		given(postGenFile.getFilePath()).willReturn(dummyPath);

		try (MockedStatic<FileUtil> fileUtil = mockStatic(FileUtil.class)) {
			postImageStorageService.deletePostImage(postGenFile);
			fileUtil.verify(() -> FileUtil.delete(dummyPath));
		}
	}
}
