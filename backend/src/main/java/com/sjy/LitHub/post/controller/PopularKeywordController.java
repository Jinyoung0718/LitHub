package com.sjy.LitHub.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.post.model.res.PopularKeywordResponseDTO;
import com.sjy.LitHub.post.service.PopularKeywordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/popular")
@SecurityRequirement(name = "accessToken")
@Tag(name = "실시간 인기 검색어 및 태그", description = "검색어 수집 및 실시간 인기 키워드 조회 API")
public class PopularKeywordController {

	private final PopularKeywordService popularKeywordService;

	@Operation(summary = "실시간 인기 검색어 조회", description = "ZSet 기반으로 상위 10개의 인기 검색어를 반환합니다.")
	@GetMapping("/keywords")
	public BaseResponse<List<PopularKeywordResponseDTO>> getPopularKeywords() {
		return BaseResponse.success(popularKeywordService.getRealtimeTopKeywords());
	}

	@Operation(summary = "실시간 인기 태그 조회", description = "ZSet 기반으로 상위 10개의 인기 태그를 반환합니다.")
	@GetMapping("/tags")
	public BaseResponse<List<PopularKeywordResponseDTO>> getPopularTags() {
		return BaseResponse.success(popularKeywordService.getRealtimeTopTags());
	}
}