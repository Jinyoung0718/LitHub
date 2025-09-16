package com.sjy.LitHub.global.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;

@MockitoSettings(strictness = Strictness.LENIENT)
class LoginFilterTest {

    @InjectMocks
    private LoginFilter loginFilter;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    @Qualifier("CachingStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        loginFilter = new LoginFilter(authenticationManager, objectMapper, userRepository, tokenService, redisTemplate);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }


    @Test
    @DisplayName("4. 로그인 성공 시 토큰 발급 및 SecurityContext 설정")
    void testSuccessfulAuthentication() {
        UserPrincipal principal = new UserPrincipal(1L, Role.ROLE_USER);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null);

        loginFilter.successfulAuthentication(request, response, filterChain, auth);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(auth, SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService).generateTokensAndSetCookies(eq(response), eq(1L), eq(Role.ROLE_USER));
    }
}