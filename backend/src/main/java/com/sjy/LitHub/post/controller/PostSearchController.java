package com.sjy.LitHub.post.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "accessToken")
@Tag(name = "게시글 검색", description = "게시글 키워드/태그 검색 및 인기 키워드 조회 API")
public class PostSearchController {

	private final SearchService searchService;

	@Operation(summary = "제목 기반 게시글 검색", description = "게시글의 제목을 기준으로 키워드 검색을 수행합니다.")
	@GetMapping("/search/title")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> searchByTitle(
		@RequestParam String keyword,
		@PageableDefault(size = 15) Pageable pageable
	) {
		return BaseResponse.success(searchService.searchByTitle(keyword, pageable));
	}

	@Operation(summary = "내용 기반 게시글 검색", description = "게시글의 본문 내용을 기준으로 키워드 검색을 수행합니다.")
	@GetMapping("/search/content")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> searchByContent(
		@RequestParam String keyword,
		@PageableDefault(size = 15) Pageable pageable
	) {
		return BaseResponse.success(searchService.searchByContent(keyword, pageable));
	}

	@Operation(summary = "제목+내용 기반 게시글 통합 검색", description = "게시글의 제목 또는 내용을 모두 기준으로 키워드 검색을 수행합니다.")
	@GetMapping("/search/combined")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> searchByTitleOrContent(
		@RequestParam String keyword,
		@PageableDefault(size = 15) Pageable pageable
	) {
		return BaseResponse.success(searchService.searchByTitleOrContent(keyword, pageable));
	}

	@Operation(summary = "태그 기반 게시글 검색", description = "지정된 태그를 기준으로 게시글 검색을 수행합니다.")
	@GetMapping("/search/tag")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> searchByTag(
		@RequestParam String tag,
		@PageableDefault(size = 15) Pageable pageable
	) {
		return BaseResponse.success(searchService.searchByTag(tag, pageable));
	}

	@Operation(summary = "실시간 인기 검색어 조회", description = "검색 시 ZSet 기반으로 수집된 실시간 인기 키워드를 조회합니다.")
	@GetMapping("/popular/keywords")
	public BaseResponse<List<String>> getPopularKeywords() {
		return BaseResponse.success(searchService.getPopularKeywordList());
	}

	@Operation(summary = "실시간 인기 태그 조회", description = "태그 검색 시 ZSet 기반으로 수집된 실시간 인기 태그를 조회합니다.")
	@GetMapping("/popular/tags")
	public BaseResponse<List<String>> getPopularTags() {
		return BaseResponse.success(searchService.getPopularTagList());
	}
}