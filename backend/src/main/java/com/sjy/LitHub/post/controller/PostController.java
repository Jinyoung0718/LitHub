package com.sjy.LitHub.post.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.service.PostImageService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.post.model.req.PostCreateRequestDTO;
import com.sjy.LitHub.post.model.req.PostUpdateRequestDTO;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;
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
	private final PostImageService postImageService;

	@Operation(summary = "게시글 생성", description = "제목, 본문, 썸네일 이미지와 태그를 포함한 게시글을 생성합니다.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<Long> createPost(
		@Valid @RequestPart("post") PostCreateRequestDTO request,
		@RequestPart("thumbnail") MultipartFile thumbnail
	) {
		Long postId = postService.createPost(request, thumbnail);
		return BaseResponse.success(postId);
	}

	@Operation(summary = "게시글 상세 조회", description = "postId를 기반으로 게시글을 상세 조회합니다.")
	@GetMapping("/{postId}")
	public BaseResponse<PostDetailResponseDTO> getPostDetail(
		@PathVariable Long postId,
		@RequestParam(defaultValue = "false") boolean isPopular
	) {
		return BaseResponse.success(postService.getPostDetail(postId, isPopular));
	}

	@Operation(summary = "게시글 수정", description = "해당 게시글의 제목, 본문, 썸네일을 수정합니다.")
	@PatchMapping("/{postId}")
	public BaseResponse<Empty> updatePost(
		@PathVariable Long postId,
		@RequestBody @Valid PostUpdateRequestDTO request
	) {
		postService.updatePost(postId, request);
		return BaseResponse.success();
	}

	@Operation(summary = "게시글 삭제", description = "해당 게시글을 삭제합니다.")
	@DeleteMapping("/{postId}")
	public BaseResponse<Empty> deletePost(
		@PathVariable Long postId
	) {
		postService.deletePost(postId);
		return BaseResponse.success();
	}

	@Operation(summary = "마크다운 이미지 업로드", description = "마크다운 본문에 삽입할 임시 이미지를 업로드합니다.")
	@PostMapping("/image")
	public BaseResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
		String imageUrl = postImageService.uploadTempMarkdownImage(file);
		return BaseResponse.success(imageUrl);
	}

	@Operation(summary = "마크다운 이미지 삭제", description = "마크다운 본문에 삽입된 임시 이미지를 삭제합니다.")
	@DeleteMapping("/image")
	public BaseResponse<Empty> deleteImage(@RequestParam String fileName) {
		postImageService.deleteTempMarkdownImage(fileName);
		return BaseResponse.success();
	}
}