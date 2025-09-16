package com.sjy.LitHub.account.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.account.model.res.FollowListResponseDTO;
import com.sjy.LitHub.account.service.follow.FollowService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.global.security.model.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@SecurityRequirement(name = "accessToken")
@Tag(name = "팔로우 관리", description = "팔로우/팔로워 조회 및 토글, 강제 삭제 API")
public class FollowController {

	private final FollowService followService;

	@Operation(summary = "팔로우 토글", description = "해당 사용자를 팔로우하거나 언팔로우합니다.")
	@PostMapping("/{followeeId}/toggle")
	public BaseResponse<Empty> toggleFollow(@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable Long followeeId) {
		followService.toggleFollow(userPrincipal.getUserId(), followeeId);
		return BaseResponse.success();
	}

	@Operation(summary = "팔로잉 목록 조회", description = "내가 팔로우한 사용자 목록을 조회합니다.")
	@GetMapping("/followings")
	public BaseResponse<PageResponse<FollowListResponseDTO>> getFollowings(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		return BaseResponse.success(followService.getFollowings(userPrincipal.getUserId(), pageable));
	}

	@Operation(summary = "팔로워 목록 조회", description = "나를 팔로우한 사용자 목록을 조회합니다.")
	@GetMapping("/followers")
	public BaseResponse<PageResponse<FollowListResponseDTO>> getFollowers(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		return BaseResponse.success(followService.getFollowers(userPrincipal.getUserId(), pageable));
	}
}