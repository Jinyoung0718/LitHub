package com.sjy.LitHub.global.security.oauth2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.oauth2.service.token.OAuthTempTokenService;
import com.sjy.LitHub.global.security.oauth2.service.token.TempTokenService;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class OAuthTempTokenServiceTest {

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private TempTokenService tempTokenService;

	@InjectMocks
	private OAuthTempTokenService oAuthTempTokenService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private final User testUser = User.builder()
		.id(1L)
		.role(Role.ROLE_USER)
		.build();

	@Test
	@DisplayName("임시 토큰 데이터 추출 - 성공")
	void extractTokenData_Success() {
		// 테스트에 필요한 쿠키 설정
		Cookie tempTokenCookie = new Cookie(AuthConst.TOKEN_TYPE_TEMP, "mocked-temp-token");
		doReturn(new Cookie[]{tempTokenCookie}).when(request).getCookies();

		// 이 테스트에서는 토큰 검증에 사용할 데이터를 stubbing
		Map<String, String> tokenData = new HashMap<>();
		tokenData.put(AuthConst.TEMP_USER_EMAIL, "test@example.com");
		tokenData.put(AuthConst.TEMP_PROVIDER, "GOOGLE");
		tokenData.put(AuthConst.TEMP_PROVIDER_ID, "provider-id");
		when(tempTokenService.validateTempSignupToken(anyString())).thenReturn(tokenData);

		Map<String, String> result = oAuthTempTokenService.extractTokenData(request);

		assertNotNull(result);
		assertEquals("test@example.com", result.get(AuthConst.TEMP_USER_EMAIL));
		assertEquals("GOOGLE", result.get(AuthConst.TEMP_PROVIDER));
	}

	@Test
	@DisplayName("임시 토큰이 없을 경우 예외 발생")
	void extractTokenData_Fail_NoTempToken() {
		// 쿠키가 아예 없도록 설정
		doReturn(null).when(request).getCookies();

		InvalidUserException exception = assertThrows(InvalidUserException.class, () -> oAuthTempTokenService.extractTokenData(request));

		assertEquals(BaseResponseStatus.USER_TEMP_SESSION_EXPIRED.getMessage(),
			exception.getStatus().getMessage());
	}

	@Test
	@DisplayName("임시 토큰이 빈 문자열일 경우 예외 발생")
	void extractTokenData_Fail_EmptyTempToken() {
		// 토큰 값이 빈 문자열인 쿠키 설정
		Cookie emptyTempTokenCookie = new Cookie(AuthConst.TOKEN_TYPE_TEMP, "");
		doReturn(new Cookie[]{emptyTempTokenCookie}).when(request).getCookies();

		InvalidUserException exception = assertThrows(InvalidUserException.class, () -> oAuthTempTokenService.extractTokenData(request));

		assertEquals(BaseResponseStatus.USER_TEMP_SESSION_EXPIRED.getMessage(),
			exception.getStatus().getMessage());
	}
}