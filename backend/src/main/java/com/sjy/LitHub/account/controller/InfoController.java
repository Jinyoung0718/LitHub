package com.sjy.LitHub.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.oauth2.service.OAuthTempTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
@Tag(name = "InfoController", description = "유저 데이터 확인 API")
public class InfoController {

	private final OAuthTempTokenService oAuthTempTokenService;

	@Operation(summary = "로그인 상태 확인", description = "현재 사용자의 인증 상태를 확인합니다.")
	@GetMapping("/check")
	public BaseResponse<Empty> checkAuth() {
		User authUser = AuthUser.getAuthUser();
		log.info("유저 로그인 체크 : {}", authUser);
		return BaseResponse.success();
	}

	@Operation(summary = "로그인 상태 확인", description = "임시 회원가입 절차 시, 임시 토큰의 만료여부 확인합니다")
	@GetMapping("/temp-check")
	public BaseResponse<Empty> checkTempToken(HttpServletRequest request) {
		oAuthTempTokenService.extractTokenData(request);
		return BaseResponse.success();
	}
}