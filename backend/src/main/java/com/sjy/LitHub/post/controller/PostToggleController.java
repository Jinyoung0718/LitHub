package com.sjy.LitHub.post.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.post.model.res.LikeResponseDTO;
import com.sjy.LitHub.post.model.res.ScrapResponseDTO;
import com.sjy.LitHub.post.service.ToggleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "accessToken")
@Tag(name = "게시글 토글", description = "좋아요 및 스크랩 토글 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostToggleController {

	private final ToggleService toggleService;

	@Operation(summary = "게시글 좋아요 토글", description = "게시글에 좋아요를 토글합니다.")
	@PostMapping("/{postId}/like")
	public BaseResponse<LikeResponseDTO> toggleLike(@PathVariable Long postId, @RequestParam(defaultValue = "false") boolean isPopular) {
		LikeResponseDTO result = toggleService.toggleLikes(postId, isPopular);
		return BaseResponse.success(result);
	}

	@Operation(summary = "게시글 스크랩 토글", description = "게시글에 스크랩을 토글합니다.")
	@PostMapping("/{postId}/scrap")
	public BaseResponse<ScrapResponseDTO> toggleScrap(@PathVariable Long postId, @RequestParam(defaultValue = "false") boolean isPopular) {
		ScrapResponseDTO result = toggleService.toggleScrap(postId, isPopular);
		return BaseResponse.success(result);
	}
}