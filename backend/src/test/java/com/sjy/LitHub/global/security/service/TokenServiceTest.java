package com.sjy.LitHub.global.security.service;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisRefreshTokenUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisRefreshTokenUtil redisRefreshTokenUtil;

    private static final Long TEST_USER_ID = 1L;
    private static final Role TEST_ROLE = Role.ROLE_USER;
    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";

    @Test
    @DisplayName("rotatingTokens() - 리프레시 토큰을 사용한 토큰 갱신")
    void testRotatingTokens() {
        String refreshToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_REFRESH, TEST_USER_ID, TEST_ROLE, AuthConst.REFRESH_EXPIRATION);
        redisRefreshTokenUtil.addRefreshToken(TEST_USER_ID, refreshToken, AuthConst.REFRESH_EXPIRATION);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_REFRESH, refreshToken));

        MockHttpServletResponse response = new MockHttpServletResponse();

        tokenService.rotatingTokens(request, response);

        Cookie newAccessCookie = response.getCookie(AuthConst.TOKEN_TYPE_ACCESS);
        Cookie newRefreshCookie = response.getCookie(AuthConst.TOKEN_TYPE_REFRESH);

        assertNotNull(newAccessCookie);
        assertNotNull(newRefreshCookie);

        String newRefreshToken = newRefreshCookie.getValue();
        assertNotEquals(refreshToken, newRefreshToken);
        assertTrue(redisRefreshTokenUtil.validateRefreshToken(TEST_USER_ID, newRefreshToken));
    }

    @Test
    @DisplayName("generateTokensAndSetCookies() - 토큰 생성 및 쿠키 설정")
    void testGenerateTokensAndSetCookies() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        tokenService.generateTokensAndSetCookies(response, TEST_USER_ID, TEST_ROLE);

        Cookie accessCookie = response.getCookie(AuthConst.TOKEN_TYPE_ACCESS);
        Cookie refreshCookie = response.getCookie(AuthConst.TOKEN_TYPE_REFRESH);

        assertNotNull(accessCookie);
        assertNotNull(refreshCookie);

        String accessToken = accessCookie.getValue();
        String refreshToken = refreshCookie.getValue();

        assertNotEquals(TEST_ACCESS_TOKEN, accessToken);
        assertNotEquals(TEST_REFRESH_TOKEN, refreshToken);

        assertTrue(redisRefreshTokenUtil.validateRefreshToken(TEST_USER_ID, refreshToken));
    }

    @Test
    @DisplayName("getAuthenticationFromToken() - 토큰에서 인증 정보 추출")
    void testGetAuthenticationFromToken() {
        String accessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, TEST_USER_ID, TEST_ROLE, AuthConst.ACCESS_EXPIRATION);

        Authentication authentication = tokenService.getAuthenticationFromToken(accessToken);

        assertNotNull(authentication);
        assertEquals(TEST_USER_ID, ((UserPrincipal) authentication.getPrincipal()).getUserId());
        assertEquals(TEST_ROLE, ((UserPrincipal) authentication.getPrincipal()).getRole());
    }

    @Test
    @DisplayName("isAccessToken() - 액세스 토큰 여부 확인")
    void testIsAccessToken() {
        String accessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, TEST_USER_ID, TEST_ROLE, AuthConst.ACCESS_EXPIRATION);

        assertTrue(tokenService.isAccessToken(accessToken));

        String refreshToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_REFRESH, TEST_USER_ID, TEST_ROLE, AuthConst.REFRESH_EXPIRATION);

        assertFalse(tokenService.isAccessToken(refreshToken));
    }
}