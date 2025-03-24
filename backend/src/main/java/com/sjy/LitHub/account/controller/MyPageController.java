package com.sjy.LitHub.account.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.PasswordUpdateRequestDTO;
import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.service.UserInfo.MyPageService;
import com.sjy.LitHub.account.service.UserInfo.ProfileImageService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.model.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "accessToken")
public class MyPageController {

	private final MyPageService myPageService;
	private final ProfileImageService profileImageService;

	@Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자 정보 및 독서 기록 조회합니다.")
	@GetMapping("/me")
	public BaseResponse<MyPageResponseDTO> getMyProfile(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(required = false) Integer year) {
		int targetYear = (year != null) ? year : LocalDate.now().getYear();
		MyPageResponseDTO myPageResponseDto = myPageService.getCachedMyPageData(userPrincipal.getUserId(), targetYear);
		return BaseResponse.success(myPageResponseDto);
	}

	@Operation(summary = "사용자 정보 조회", description = "특정 사용자의 프로필을 조회합니다.")
	@GetMapping("/{userId}")
	public BaseResponse<MyPageResponseDTO> getUserProfile(
		@PathVariable Long userId,
		@RequestParam(required = false) Integer year) {
		int targetYear = (year != null) ? year : LocalDate.now().getYear();
		MyPageResponseDTO userPageResponseDto = myPageService.getCachedMyPageData(userId, targetYear);
		return BaseResponse.success(userPageResponseDto);
	}

	@Operation(summary = "프로필 이미지 업로드", description = "사용자가 프로필 이미지를 업로드합니다. 기존 프로필 이미지는 덮어쓰기됩니다.")
	@PostMapping(value = "/profile/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<Map<String, String>> uploadProfileImage(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam("file") MultipartFile file) {
		Map<String, String> imageUrls = profileImageService.saveUserImage(file, userPrincipal.getUsername());
		return BaseResponse.success(imageUrls);
	}

	@Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 기본 이미지로 변경합니다.")
	@DeleteMapping("/profile/delete")
	public BaseResponse<Empty> deleteProfileImage(
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		profileImageService.deleteUserImage(userPrincipal.getUsername());
		return BaseResponse.success();
	}

	@Operation(summary = "독서 기록 저장", description = "독서 시간을 추가하고 독서 연속 기록과 색상 레벨을 갱신합니다.")
	@PostMapping("/reading-log")
	public BaseResponse<MyPageResponseDTO> saveReadingRecord(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam int minutes) {
		MyPageResponseDTO myPageResponseDto = myPageService.saveReadingRecordAndUpdateCache(userPrincipal.getUserId(), minutes);
		return BaseResponse.success(myPageResponseDto);
	}

	@Operation(summary = "닉네임 수정", description = "중복조회 및 형식 검사를 한 후 닉네임을 수정하는 기능입니다.")
	@PatchMapping("/nickname")
	public BaseResponse<Empty> updateNickName(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid NicknameRequestDTO requestDto) {
		myPageService.updateNickName(userPrincipal.getUserId(), requestDto);
		return BaseResponse.success();
	}

	@Operation(summary = "비밀번호 수정", description = "비밀번호 확인 후, 비밀번호 변경")
	@PatchMapping("/me/password")
	public BaseResponse<Empty> updatePassword(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid PasswordUpdateRequestDTO requestDto) {
		myPageService.updatePassword(userPrincipal.getUserId(), requestDto);
		return BaseResponse.success();
	}

	@Operation(summary = "계정 삭제", description = "로그인한 사용자의 계정을 논리 삭제 처리합니다.")
	@PostMapping("/delete-user")
	public BaseResponse<Empty> deleteUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		myPageService.deleteUser(userPrincipal.getUserId());
		return BaseResponse.success();
	}
}