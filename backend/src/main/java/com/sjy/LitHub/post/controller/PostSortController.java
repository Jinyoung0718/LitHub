package com.sjy.LitHub.post.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.service.FeedService;
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

	private final FeedService feedService;
	private final SortService sortService;

	@Operation(summary = "내가 작성한 게시글 조회", description = "내가 작성한 게시글 목록을 조회합니다.")
	@GetMapping("/mine")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> getMyPosts(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.getMyPosts(pageable));
	}

	@Operation(summary = "스크랩한 게시글 조회", description = "내가 스크랩한 게시글 목록을 조회합니다.")
	@GetMapping("/scraps")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> getScrappedPosts(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.getScrappedPosts(pageable));
	}

	@Operation(summary = "일간 인기 게시글 조회", description = "좋아요 + 스크랩 기준으로 인기 게시글을 정렬합니다.")
	@GetMapping("/popular")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> getPopularPosts(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(sortService.findDailyPopularPosts(pageable));
	}

	@Operation(summary = "팔로워 게시글 조회", description = "인플루어서 게시글 + 팔로워 게시글")
	@GetMapping("/feed")
	public BaseResponse<PageResponse<PostSummaryResponseDTO>> getFollowerFeed(
		@PageableDefault(size = 15) Pageable pageable) {

		return BaseResponse.success(feedService.getMergedFollowerFeed(pageable));
	}
}