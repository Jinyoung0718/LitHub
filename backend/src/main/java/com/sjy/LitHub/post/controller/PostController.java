package com.sjy.LitHub.post.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.service.post.MarkdownFileService;
import com.sjy.LitHub.file.service.post.ThumbnailPostService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.post.model.req.PostCreateRequestDTO;
import com.sjy.LitHub.post.model.req.PostContentUpdateDTO;
import com.sjy.LitHub.post.model.req.UploadImageResponseDTO;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;
import com.sjy.LitHub.post.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@SecurityRequirement(name = "accessToken")
@Tag(name = "게시글 관리", description = "게시글 생성, 수정, 삭제, 상세 조회 API")
public class PostController {

	private final PostService postService;
	private final ThumbnailPostService thumbnailPostService;
	private final MarkdownFileService markdownFileService;

	@Operation(summary = "게시글 생성", description = "제목, 본문, 썸네일 storageKey 및 태그를 포함한 게시글을 생성합니다.")
	@PostMapping
	public BaseResponse<Long> createPost(@Valid @RequestBody PostCreateRequestDTO request) {
		return BaseResponse.success(postService.createPost(request));
	}

	@Operation(summary = "게시글 상세 조회", description = "postId를 기반으로 게시글을 상세 조회합니다.")
	@GetMapping("/{postId}")
	public BaseResponse<PostDetailResponseDTO> getPostDetail(@PathVariable Long postId) {
		return BaseResponse.success(postService.getPostDetail(postId));
	}

	@Operation(summary = "게시글 제목/본문 수정")
	@PatchMapping("/{postId}")
	public BaseResponse<Empty> updatePostContent(@PathVariable Long postId,
		@Valid @RequestBody PostContentUpdateDTO request) {
		postService.updatePostContent(postId, request);
		return BaseResponse.success();
	}

	@Operation(summary = "게시글 삭제", description = "해당 게시글을 삭제합니다.")
	@DeleteMapping("/{postId}")
	public BaseResponse<Empty> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return BaseResponse.success();
	}

	@Operation(summary = "썸네일 이미지 업로드", description = "게시글 썸네일 이미지를 업로드하고 URL 과 storageKey 를 반환합니다.")
	@PostMapping("/thumbnail")
	public BaseResponse<UploadImageResponseDTO> uploadThumbnail(
		@RequestParam("file") MultipartFile file
	) {
		return BaseResponse.success(thumbnailPostService.uploadTempThumbnailImage(file));
	}

	@Operation(summary = "썸네일 이미지 수정")
	@PatchMapping("/{postId}/thumbnail")
	public BaseResponse<Empty> updateThumbnail(
		@PathVariable Long postId,
		@RequestParam("file") MultipartFile thumbnail
	) {
		postService.updatePostThumbnail(postId, thumbnail);
		return BaseResponse.success();
	}

	@Operation(summary = "마크다운 이미지 업로드", description = "마크다운 본문에서 사용할 임시 이미지를 업로드하고 퍼블릭 URL 과 storageKey 를 반환합니다.")
	@PostMapping("/images")
	public BaseResponse<UploadImageResponseDTO> uploadMarkdownImage(
		@RequestParam("file") MultipartFile file
	) {
		return BaseResponse.success(markdownFileService.uploadTempMarkdownImage(file));
	}

	@Operation(summary = "마크다운 이미지 삭제", description = "마크다운 본문에서 사용된 임시 이미지를 삭제합니다. (post 에 연결되지 않은 경우만 삭제 가능)")
	@DeleteMapping("/images/{storageKey}")
	public BaseResponse<Empty> deleteMarkdownImage(
		@PathVariable String storageKey
	) {
		markdownFileService.deleteTempMarkdownImage(storageKey);
		return BaseResponse.success();
	}
}