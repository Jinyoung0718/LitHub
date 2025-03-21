package com.sjy.LitHub.global.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, UserRepository userRepository, TokenService tokenService) {
        super.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        setFilterProcessesUrl("/api/auth/basic/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            Map<String, String> credentials = objectMapper.readValue(req.getInputStream(), new TypeReference<>() {});
            String userEmail = credentials.get("username");
            String password = credentials.get("password");

            log.info("로그인 시도: 이메일={}, 비밀번호={}", userEmail, password);

            if (isAccountDeleted(userEmail)) {
                SecurityContextHolder.clearContext();
                req.setAttribute("exception", new InvalidAuthenticationException(BaseResponseStatus.USER_LOGIN_RECOVERY_REQUIRED));
                throw new BadCredentialsException(BaseResponseStatus.USER_LOGIN_RECOVERY_REQUIRED.getMessage()); // Security 흐름 타도록 변경
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, password);
            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            SecurityContextHolder.clearContext();
            req.setAttribute("exception", new InvalidAuthenticationException(BaseResponseStatus.AUTH_REQUEST_BODY_INVALID));
            throw new BadCredentialsException(BaseResponseStatus.AUTH_REQUEST_BODY_INVALID.getMessage()); // Security 흐름 타도록 변경
        }
    }

    // 논리 삭제 유저 확인
    private boolean isAccountDeleted(String userEmail) {
        return userRepository.findByUserEmailDeleted(userEmail).isEmpty();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUserId();
        Role role = userPrincipal.getRole();

        tokenService.generateTokensAndSetCookies(response, userId, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.error("인증 실패: {}", failed.getMessage());
        log.error("인증 예외 유형: {}", failed.getClass().getSimpleName());

        Exception exception = (Exception) request.getAttribute("exception");

        BaseResponse<Empty> errorResponse = (exception instanceof InvalidAuthenticationException invalidAuthEx)
                ? BaseResponse.error(invalidAuthEx.getStatus())
                : BaseResponse.error(BaseResponseStatus.UNAUTHORIZED);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(errorResponse.getHttpStatus());
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}