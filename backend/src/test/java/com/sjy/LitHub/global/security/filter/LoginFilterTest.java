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

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        loginFilter = new LoginFilter(authenticationManager, objectMapper, userRepository, tokenService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("1. 정상 로그인 시 AuthenticationManager 호출")
    void testAttemptAuthenticationSuccess() throws Exception {
        String email = "test@example.com";
        String password = "korean12@";

        Map<String, String> credentials = Map.of("username", email, "password", password);
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
            .thenReturn(credentials);

        User normalUser = User.builder()
            .userEmail(email)
            .nickName("normal")
            .password("encoded")
            .deletedAt(null)
            .build();

        when(userRepository.findByUserEmailAll(email)).thenReturn(Optional.of(normalUser));
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(mock(Authentication.class));

        Authentication auth = loginFilter.attemptAuthentication(request, response);
        assertNotNull(auth);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("2. 요청 본문이 잘못된 경우 BadCredentialsException 발생")
    void testAttemptAuthenticationWithInvalidBody() throws IOException {
        // JSON 파싱 실패를 유도하는 MockInputStream
        MockHttpServletRequest badRequest = new MockHttpServletRequest() {
            @Override
            public ServletInputStream getInputStream() {
                return new DelegatingServletInputStream(
                    new ByteArrayInputStream("}".getBytes(StandardCharsets.UTF_8))
                );
            }
        };

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
            .thenThrow(new IOException("잘못된 JSON"));

        assertThrows(BadCredentialsException.class, () -> loginFilter.attemptAuthentication(badRequest, response));
        assertEquals(BaseResponseStatus.AUTH_REQUEST_BODY_INVALID.getCode(),
            ((InvalidAuthenticationException) badRequest.getAttribute("exception")).getStatus().getCode());
    }

    @Test
    @DisplayName("3. 논리 삭제된 계정 로그인 시 BadCredentialsException 발생")
    void testAttemptAuthenticationWithDeletedUser() throws Exception {
        String email = "deleted@example.com";
        String password = "pass";
        Map<String, String> credentials = Map.of("username", email, "password", password);

        User deletedUser = User.builder()
            .userEmail(email)
            .nickName("deleted")
            .password("encoded")
            .deletedAt(LocalDateTime.now())
            .build();

        when(userRepository.findByUserEmailDeleted(email)).thenReturn(Optional.of(deletedUser));

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
            .thenReturn(credentials);

        assertThrows(BadCredentialsException.class, () -> loginFilter.attemptAuthentication(request, response));
        assertInstanceOf(InvalidAuthenticationException.class, request.getAttribute("exception"));
        assertEquals(BaseResponseStatus.USER_LOGIN_RECOVERY_REQUIRED.getCode(),
            ((InvalidAuthenticationException)Objects.requireNonNull(request.getAttribute("exception"))).getStatus().getCode());
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