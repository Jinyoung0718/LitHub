package com.sjy.LitHub.global.security.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisRefreshTokenUtil;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RedisRefreshTokenUtil redisRefreshTokenUtil;

    @Mock
    private JwtUtil jwtUtil;

    private final Long userId = 1L;
    private final Role role = Role.ROLE_USER;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("1. rotatingTokens - 만료된 리프레시 토큰 → 예외 발생")
    void testRotatingTokens_expired() {
        String refreshToken = "expired-refresh-token";
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_REFRESH, refreshToken));

        when(jwtUtil.isExpired(refreshToken)).thenReturn(true);

        assertThrows(InvalidAuthenticationException.class, () ->
            tokenService.rotatingTokens(request, response));
    }

    @Test
    @DisplayName("3. getAuthenticationFromToken - 인증 객체 추출")
    void testGetAuthenticationFromToken() {
        String token = "some-token";

        when(jwtUtil.getUserId(token)).thenReturn(userId);
        when(jwtUtil.getRole(token)).thenReturn(role);

        Authentication auth = tokenService.getAuthenticationFromToken(token);
        assertNotNull(auth);

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        assertEquals(userId, principal.getUserId());
        assertEquals(role, principal.getRole());
    }

    @Test
    @DisplayName("4. isAccessToken - 액세스 토큰 여부 판단")
    void testIsAccessToken() {
        when(jwtUtil.getCategory("access-token")).thenReturn("access");
        when(jwtUtil.getCategory("refresh-token")).thenReturn("refresh");

        assertTrue(tokenService.isAccessToken("access-token"));
        assertFalse(tokenService.isAccessToken("refresh-token"));
    }
}
