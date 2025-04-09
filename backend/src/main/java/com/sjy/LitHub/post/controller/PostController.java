package com.sjy.LitHub.post.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.service.PostImageService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.post.model.req.PostCreateRequestDTO;
import com.sjy.LitHub.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

	private final PostService postService;
	private final PostImageService postImageService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<Long> createPost(
		@Valid @RequestPart("post") PostCreateRequestDTO request,
		@RequestPart("thumbnail") MultipartFile thumbnail
	) {
		Long postId = postService.createPost(request, thumbnail);
		return BaseResponse.success(postId);
	}

	@PostMapping("/image")
	public BaseResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
		String imageUrl = postImageService.uploadTempMarkdownImage(file);
		return BaseResponse.success(imageUrl);
	}

	@DeleteMapping("/image")
	public BaseResponse<Empty> deleteImage(@RequestParam String fileName) {
		postImageService.deleteTempMarkdownImage(fileName);
		return BaseResponse.success();
	}
}