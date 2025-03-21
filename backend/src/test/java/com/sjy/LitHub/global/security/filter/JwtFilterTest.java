package com.sjy.LitHub.global.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.exception.custom.InvalidTokenException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.service.TokenService;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisBlacklistUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private RedisBlacklistUtil redisBlacklistUtil;

    @Mock
    private TokenService tokenService;

    @Spy
    private JwtUtil jwtUtil = new JwtUtil("aGVsbG93b3JsZC1oZWxsb3dvcmxkLXRlc3Qtc2VjcmV0LXRlc3Qtc2VjcmV0LXRlc3Q="); // 256비트 base64

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    private String validAccessToken;
    private String expiredAccessToken;
    private static final Long USER_ID = 1L;
    private static final Role ROLE_USER = Role.ROLE_USER;

    @BeforeEach
    void setup() {
        jwtFilter = new JwtFilter(redisBlacklistUtil, tokenService, jwtUtil);

        validAccessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, USER_ID, ROLE_USER, 600000);
        expiredAccessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, USER_ID, ROLE_USER, 1);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("1. 인증 제외 경로는 필터 우회")
    void testWhitelistBypass() throws Exception {
        request.setRequestURI("/api/auth/login");
        jwtFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("2. 액세스 토큰 누락 시 401 반환")
    void testMissingAccessToken() throws Exception {
        jwtFilter.doFilter(request, response, filterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("3. 블랙리스트 토큰은 401 반환")
    void testBlacklistedToken() throws Exception {
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, validAccessToken));
        when(redisBlacklistUtil.isInBlackList(validAccessToken)).thenReturn(true);

        jwtFilter.doFilter(request, response, filterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("4. 만료된 토큰은 rotating 로직 호출")
    void testExpiredTokenTriggersRefresh() throws Exception {
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, expiredAccessToken));
        when(redisBlacklistUtil.isInBlackList(expiredAccessToken)).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);
        verify(tokenService).rotatingTokens(any(), any());
    }

    @Test
    @DisplayName("5. 리프레시 토큰도 만료되면 예외 반환")
    void testRefreshTokenExpired() throws Exception {
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, expiredAccessToken));
        when(redisBlacklistUtil.isInBlackList(expiredAccessToken)).thenReturn(false);
        doThrow(new InvalidAuthenticationException(BaseResponseStatus.REFRESH_TOKEN_EXPIRED))
            .when(tokenService).rotatingTokens(any(), any());

        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertInstanceOf(InvalidAuthenticationException.class, request.getAttribute("exception"));
    }

    @Test
    @DisplayName("6. 토큰 형식은 맞지만 카테고리가 access 가 아니면 401")
    void testInvalidCategoryToken() throws Exception {
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, validAccessToken));
        when(redisBlacklistUtil.isInBlackList(validAccessToken)).thenReturn(false);
        doReturn(false).when(tokenService).isAccessToken(validAccessToken); // spy기반 mocking

        jwtFilter.doFilter(request, response, filterChain);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("7. 유효한 토큰이면 SecurityContext 설정")
    void testValidAccessToken() throws Exception {
        Authentication mockAuth = new UsernamePasswordAuthenticationToken("principal", null, List.of());
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, validAccessToken));

        when(redisBlacklistUtil.isInBlackList(validAccessToken)).thenReturn(false);
        when(tokenService.isAccessToken(validAccessToken)).thenReturn(true);
        when(tokenService.getAuthenticationFromToken(validAccessToken)).thenReturn(mockAuth);

        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(mockAuth, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("8. 구조가 잘못된 JWT 는 예외 반환")
    void testMalformedJwtToken() throws Exception {
        String brokenToken = "not.valid.jwt.token";
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, brokenToken));

        when(redisBlacklistUtil.isInBlackList(brokenToken)).thenReturn(false);
        doThrow(new JwtException("잘못된 형식")).when(jwtUtil).isExpired(brokenToken);

        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertInstanceOf(InvalidTokenException.class, request.getAttribute("exception"));
    }
}