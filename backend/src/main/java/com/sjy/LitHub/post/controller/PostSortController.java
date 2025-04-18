package com.sjy.LitHub.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.service.SortService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 조회 및 정렬", description = "게시글 검색 및 인기 정렬 API")
@SecurityRequirement(name = "accessToken")
public class PostSortController {

	private final SortService sortService;

	@Operation(summary = "키워드 기반 게시글 검색", description = "제목 기준으로 게시글을 검색합니다.")
	@GetMapping("/search")
	public BaseResponse<Page<PostSummaryResponseDTO>> searchByKeyword(
		@RequestParam String keyword,
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.searchByKeyword(keyword, pageable));
	}

	@Operation(summary = "태그 기반 게시글 검색", description = "태그 기준으로 게시글을 검색합니다.")
	@GetMapping("/tag")
	public BaseResponse<Page<PostSummaryResponseDTO>> searchByTag(
		@RequestParam String tag,
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.searchByTag(tag, pageable));
	}

	@Operation(summary = "일간 인기 게시글 조회", description = "좋아요 + 스크랩 기준으로 인기 게시글을 정렬합니다.")
	@GetMapping("/popular")
	public BaseResponse<Page<PostSummaryResponseDTO>> getPopularPosts(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.findDailyPopularPosts(pageable));
	}

	@Operation(summary = "내가 작성한 게시글 조회", description = "내가 작성한 게시글 목록을 조회합니다.")
	@GetMapping("/mine")
	public BaseResponse<Page<PostSummaryResponseDTO>> getMyPosts(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.getMyPosts(pageable));
	}

	@Operation(summary = "스크랩한 게시글 조회", description = "내가 스크랩한 게시글 목록을 조회합니다.")
	@GetMapping("/scraps")
	public BaseResponse<Page<PostSummaryResponseDTO>> getScrappedPosts(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.getScrappedPosts(pageable));
	}

	@Operation(summary = "팔로우 피드 조회", description = "팔로우한 유저들의 30일 간의 게시글을 인기 기반 정렬로 조회합니다.")
	@GetMapping("/feed")
	public BaseResponse<Page<PostSummaryResponseDTO>> getFollowerFeed(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.getFollowerFeed(pageable));
	}
}