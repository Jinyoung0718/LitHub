package com.sjy.LitHub.file.service.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.mapper.PostGenFileMapper;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.model.req.UploadImageResponseDTO;

@ExtendWith(MockitoExtension.class)
class ThumbnailImageServiceTest {

	@Mock
	private PostImageStorage postImageStorage;

	@Mock
	private PostGenFileMapper postGenFileMapper;

	@Mock
	private PostGenFileRepository postGenFileRepository;

	@InjectMocks
	private ThumbnailImageService thumbnailImageService;

	@Test
	@DisplayName("썸네일 임시 업로드 성공")
	void uploadTempThumbnailImage_success() {
		MultipartFile file = new MockMultipartFile("file", "thumb.png", "image/png", "fake".getBytes());
		User user = new User(1L);
		PostGenFile mockImage = mock(PostGenFile.class);

		try (MockedStatic<AuthUser> authMock = mockStatic(AuthUser.class)) {
			authMock.when(AuthUser::getAuthUser).thenReturn(user);
			given(postGenFileMapper.toEntity(user, file, PostGenFile.TypeCode.THUMBNAIL)).willReturn(mockImage);
			given(mockImage.getPublicUrl()).willReturn("/gen/1/thumb.png");
			given(mockImage.getFileName()).willReturn("thumb.png");

			UploadImageResponseDTO response = thumbnailImageService.uploadTempThumbnailImage(file);

			then(postImageStorage).should().savePostImage(file, mockImage);
			then(postGenFileRepository).should().save(mockImage);
			assertThat(response.getUrl()).isEqualTo("/gen/1/thumb.png");
			assertThat(response.getFileName()).isEqualTo("thumb.png");
		}
	}

	@Test
	@DisplayName("썸네일 게시글에 연결")
	void assignThumbnailToPost_success() {
		Post post = Post.builder().id(1L).user(new User(1L)).build();
		PostGenFile thumb = mock(PostGenFile.class);

		given(postGenFileRepository.findByFileNameAndUserIdAndTypeCode("thumb.webp", 1L, PostGenFile.TypeCode.THUMBNAIL))
			.willReturn(Optional.of(thumb));

		thumbnailImageService.assignThumbnailToPost(post, "thumb.webp", 1L);

		assertThat(post.getImages().get(PostGenFile.TypeCode.THUMBNAIL)).isEqualTo(thumb);
	}

	@Test
	@DisplayName("썸네일 게시글 연결 실패 시 예외 발생")
	void assignThumbnailToPost_notFound() {
		Post post = Post.builder().id(1L).user(new User(1L)).build();

		given(postGenFileRepository.findByFileNameAndUserIdAndTypeCode("thumb.webp", 1L, PostGenFile.TypeCode.THUMBNAIL))
			.willReturn(Optional.empty());

		assertThatThrownBy(() -> thumbnailImageService.assignThumbnailToPost(post, "thumb.webp", 1L))
			.isInstanceOf(InvalidFileException.class);
	}

	@Test
	@DisplayName("썸네일 교체 - 기존 썸네일 존재")
	void updateThumbnail_withExisting() {
		Post post = Post.builder().user(new User(1L)).build();
		MultipartFile file = new MockMultipartFile("f", "new.png", "image/png", "x".getBytes());

		PostGenFile existing = mock(PostGenFile.class);
		post.addImage(existing);

		thumbnailImageService.updateThumbnail(post, file);

		then(postImageStorage).should().savePostImage(file, existing);
		then(postGenFileMapper).should().updateMetadata(existing, file);
	}

	@Test
	@DisplayName("썸네일 교체 - 기존 썸네일 없음")
	void updateThumbnail_withoutExisting() {
		Post post = Post.builder().user(new User(1L)).build();
		MultipartFile file = new MockMultipartFile("f", "new.png", "image/png", "x".getBytes());
		PostGenFile newThumb = mock(PostGenFile.class);

		given(postGenFileMapper.toEntity(post.getUser(), file, PostGenFile.TypeCode.THUMBNAIL)).willReturn(newThumb);

		thumbnailImageService.updateThumbnail(post, file);

		then(postImageStorage).should().savePostImage(file, newThumb);
		assertThat(post.getImages().get(PostGenFile.TypeCode.THUMBNAIL)).isEqualTo(newThumb);
	}
}
