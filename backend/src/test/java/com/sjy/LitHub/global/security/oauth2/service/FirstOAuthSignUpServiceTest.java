package com.sjy.LitHub.global.security.oauth2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.util.AuthConst;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class FirstOAuthSignUpServiceTest {

    @InjectMocks
    private FirstOAuthSignUpService firstOAuthSignUpService;

    @Mock
    private OAuthSignupService oAuthSignupService;

    @Mock
    private OAuthTempTokenService oAuthTempTokenService;

    @Mock
    private OAuthUserTempService oAuthUserTempService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final SocialSignupDTO signupDTO = new SocialSignupDTO("nickname", "password123!");
    private final Map<String, String> tokenData = Map.of(
        AuthConst.TEMP_USER_EMAIL, "test@example.com",
        AuthConst.TEMP_PROVIDER, "GOOGLE",
        AuthConst.TEMP_PROVIDER_ID, "provider123"
    );

    private final User dummyUser = User.builder()
        .id(1L)
        .userEmail("test@example.com")
        .nickName("nickname")
        .role(Role.ROLE_USER)
        .build();

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("1. 정상적인 소셜 회원가입 요청 처리")
    void testFinalizeSocialSignup_success() {
        when(oAuthTempTokenService.extractTokenData(request)).thenReturn(tokenData);
        when(oAuthSignupService.processSignup(signupDTO, tokenData)).thenReturn(dummyUser);
        doNothing().when(oAuthTempTokenService).generateAndSetTokens(response, dummyUser);
        doNothing().when(oAuthUserTempService).deleteTempOAuthUser("test@example.com");

        firstOAuthSignUpService.finalizeSocialSignup(request, response, signupDTO);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(1L, ((UserPrincipal) auth.getPrincipal()).getUserId());
        assertEquals(Role.ROLE_USER, ((UserPrincipal) auth.getPrincipal()).getRole());
        verify(oAuthTempTokenService).generateAndSetTokens(response, dummyUser);
        verify(oAuthUserTempService).deleteTempOAuthUser("test@example.com");
    }

    @Test
    @DisplayName("2. OAuthTempTokenService 에서 예외 발생 시 회원가입 실패")
    void testFinalizeSocialSignup_tempTokenException() {
        when(oAuthTempTokenService.extractTokenData(request)).thenThrow(new InvalidUserException(BaseResponseStatus.USER_TEMP_SESSION_EXPIRED));

        assertThrows(InvalidUserException.class, () -> firstOAuthSignUpService.finalizeSocialSignup(request, response, signupDTO));
        verify(oAuthSignupService, never()).processSignup(any(), any());
        verify(oAuthTempTokenService, never()).generateAndSetTokens(any(), any());
    }

    @Test
    @DisplayName("3. OAuthSignupService 에서 예외 발생 시 회원가입 실패")
    void testFinalizeSocialSignup_signupFail() {
        when(oAuthTempTokenService.extractTokenData(request)).thenReturn(tokenData);
        when(oAuthSignupService.processSignup(signupDTO, tokenData)).thenThrow(new InvalidUserException(BaseResponseStatus.USER_PASSWORD_NOT_VALID));

        assertThrows(InvalidUserException.class, () -> firstOAuthSignUpService.finalizeSocialSignup(request, response, signupDTO));
        verify(oAuthTempTokenService, never()).generateAndSetTokens(any(), any());
    }

    @Test
    @DisplayName("4. 인증 객체가 SecurityContext 에 설정되는지 확인")
    void testSecurityContextIsSetCorrectly() {
        when(oAuthTempTokenService.extractTokenData(request)).thenReturn(tokenData);
        when(oAuthSignupService.processSignup(signupDTO, tokenData)).thenReturn(dummyUser);
        doNothing().when(oAuthTempTokenService).generateAndSetTokens(response, dummyUser);
        doNothing().when(oAuthUserTempService).deleteTempOAuthUser("test@example.com");

        firstOAuthSignUpService.finalizeSocialSignup(request, response, signupDTO);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
		assertInstanceOf(UserPrincipal.class, auth.getPrincipal());
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}