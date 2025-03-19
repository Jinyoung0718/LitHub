package com.sjy.LitHub.account.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.signup.EmailAuthDTO;
import com.sjy.LitHub.account.model.req.signup.EmailAuthVerificationDTO;
import com.sjy.LitHub.account.model.req.signup.SignupDTO;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.account.service.auth.AuthService;
import com.sjy.LitHub.account.service.email.EmailService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.oauth2.service.FirstOAuthSignUpService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "계정 관련 API")
public class AuthController {

    private final AuthService authService;
    private final FirstOAuthSignUpService FirstOAuthSignUpService;
    private final EmailService emailService;

    @Operation(summary = "이메일 인증 코드 전송")
    @PostMapping("/send-email-code")
    public BaseResponse<Empty> sendEmailVerificationCode(@RequestBody @Valid EmailAuthDTO emailAuthDto) {
        emailService.sendEmailVerificationCode(emailAuthDto.getEmail());
        return BaseResponse.success();
    }

    @Operation(summary = "이메일 인증 코드 검증")
    @PostMapping("/verify-email")
    public BaseResponse<Empty> verifyEmailCode(@RequestBody @Valid EmailAuthVerificationDTO emailAuthVerificationDto) {
        emailService.verifyEmailCode(emailAuthVerificationDto.getEmail(), emailAuthVerificationDto.getCode());
        return BaseResponse.success();
    }

    @Operation(summary = "닉네임 검증", description = "길이 및 빈값 검증을 합니다.")
    @PostMapping("/check-nickname")
    public BaseResponse<Empty> checkNickname(@RequestBody @Valid NicknameRequestDTO request) {
        authService.validateNicknameAvailability(request.getNickName());
        return BaseResponse.success();
    }

    @Operation(summary = "계정 복구", description = "deleteAt에 값이 있었던 회원을 다시 null 값으로 되돌립니다.")
    @PostMapping("/restore-user")
    public BaseResponse<Empty> restoreUser(@RequestParam String email) {
        authService.restoreUser(email);
        return BaseResponse.success();
    }

    @Operation(summary = "자체 회원가입", description = "회원가입의 경우 두 가지로 나뉩니다. 해당 컨트롤러는 자체 회원가입 입니다.")
    @PostMapping("/signup")
    public BaseResponse<Empty> signup(@RequestBody @Valid SignupDTO signupDTO) {
        authService.signup(signupDTO);
        return BaseResponse.success();
    }

    @Operation(summary = "소셜 간편 회원가입", description = "회원가입의 경우 두 가지로 나뉩니다. 해당 컨트롤러는 소셜 간편 회원가입 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "임시 토큰이 만료되었습니다. 프론트엔드는 로그인 페이지로 리다이렉트해야 합니다.")
    })
    @PostMapping("/social-signup")
    public BaseResponse<Empty> completeSignup(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SocialSignupDTO socialSignupDto) {
        FirstOAuthSignUpService.finalizeSocialSignup(request, response, socialSignupDto);
        return BaseResponse.success();
    }
}