package com.sjy.LitHub.file.service.post;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.mapper.PostGenFileMapper;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.post.entity.Post;

@ExtendWith(MockitoExtension.class)
class MarkdownImageServiceTest {

	@Mock
	private PostImageStorage postImageStorage;
	@Mock private PostGenFileMapper postGenFileMapper;
	@Mock private PostGenFileRepository postGenFileRepository;

	@InjectMocks
	private MarkdownImageService markdownImageService;

	private final Long testUserId = 1L;

	@BeforeEach
	void setupSecurityContext() {
		UserPrincipal principal = new UserPrincipal(testUserId, Role.ROLE_USER);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@DisplayName("마크다운 이미지 업로드 성공")
	void uploadTempMarkdownImage_success() {
		MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "x".getBytes());
		PostGenFile mockFile = Mockito.mock(PostGenFile.class);
		given(postGenFileMapper.toEntity(any(User.class), any(), any())).willReturn(mockFile);
		given(mockFile.getPublicUrl()).willReturn("/gen/1/test.png");

		String url = markdownImageService.uploadTempMarkdownImage(file);

		assertThat(url).isEqualTo("/gen/1/test.png");
		then(postImageStorage).should().savePostImage(file, mockFile);
		then(postGenFileRepository).should().save(mockFile);
	}

	@Test
	@DisplayName("마크다운 본문에서 이미지 파일명 추출")
	void extractImageFileNames_success() {
		String markdown = """
            ![설명](/gen/1/a.webp)
            ![다른설명](/gen/1/b.webp)
            """;

		List<String> result = markdownImageService.extractImageFileNames(markdown);

		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0)).isEqualTo("a.webp");
		assertThat(result.get(1)).isEqualTo("b.webp");
	}

	@Test
	@DisplayName("존재하지 않는 이미지 삭제 시 예외 발생")
	void deleteTempMarkdownImage_notFound() {
		given(postGenFileRepository.findByFileNameAndUserIdAndPostIsNull(anyString(), anyLong())).willReturn(Optional.empty());

		assertThatThrownBy(() -> markdownImageService.deleteTempMarkdownImage("nope.webp"))
			.isInstanceOf(InvalidFileException.class);
	}

	@Test
	@DisplayName("마크다운 이미지 동기화 - 사용되지 않은 이미지 삭제")
	void syncMarkdownImages_deletesUnused() {
		Post post = Post.builder().id(1L).user(User.builder().id(testUserId).build()).build();

		PostGenFile used = Mockito.mock(PostGenFile.class);
		PostGenFile unused = Mockito.mock(PostGenFile.class);
		given(unused.getFileName()).willReturn("unused123.webp");

		given(postGenFileRepository.findMarkdownImagesByPostId(1L)).willReturn(List.of(used, unused));
		given(postGenFileRepository.findAllByFileNameInAndUserIdAndTypeCode(anyList(), anyLong(), any())).willReturn(List.of());

		String markdown = "![내용](/gen/1/used123.webp)";
		markdownImageService.syncMarkdownImages(post, markdown);

		then(postImageStorage).should().deletePostImage(unused);
		then(postGenFileRepository).should().delete(unused);
	}
}
