package com.sjy.LitHub.global.security.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.service.TokenService;
import com.sjy.LitHub.global.security.util.AuthConst;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public LoginFilter(AuthenticationManager authenticationManager,
        ObjectMapper objectMapper,
        UserRepository userRepository,
        TokenService tokenService,
        @Qualifier("CachingStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        super.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.redisTemplate = redisTemplate;
        setFilterProcessesUrl("/api/auth/basic/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        String clientIp = extractClientIp(req);
        String key = AuthConst.LOGIN_FAIL_PREFIX + clientIp;

        // 차단 여부 확인
        String failCountStr = redisTemplate.opsForValue().get(key);
        int failCount = (failCountStr == null) ? 0 : Integer.parseInt(failCountStr);

        if (failCount >= AuthConst.MAX_LOGIN_ATTEMPTS) {
            throw new InvalidAuthenticationException(BaseResponseStatus.LOGIN_TOO_MANY_ATTEMPTS);
        }

        try {
            Map<String, String> credentials = objectMapper.readValue(req.getInputStream(), new TypeReference<>() {});
            String userEmail = credentials.get(AuthConst.LOGIN_USERNAME);
            String password = credentials.get(AuthConst.LOGIN_PASSWORD);

            if (isAccountDeleted(userEmail)) {
                throw new InvalidAuthenticationException(BaseResponseStatus.USER_LOGIN_RECOVERY_REQUIRED);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, password);
            return getAuthenticationManager().authenticate(authToken);

        } catch (IOException e) {
            throw new InvalidAuthenticationException(BaseResponseStatus.AUTH_REQUEST_BODY_INVALID);
        }
    }

    private boolean isAccountDeleted(String userEmail) {
        return userRepository.findByUserEmailDeleted(userEmail).isPresent();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUserId();
        Role role = userPrincipal.getRole();

        // 로그인 성공 시 실패 카운트 초기화
        String clientIp = extractClientIp(request);
        redisTemplate.delete(AuthConst.LOGIN_FAIL_PREFIX + clientIp);

        tokenService.invalidatePreviousTokens(request);
        tokenService.generateTokensAndSetCookies(response, userId, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        String clientIp = extractClientIp(request);
        String key = AuthConst.LOGIN_FAIL_PREFIX + clientIp;

        Long newCount = redisTemplate.opsForValue().increment(key);
        long attempts = (newCount != null) ? newCount : 1L;

        if (attempts == 1L) {
            redisTemplate.expire(key, AuthConst.LOGIN_BLOCK_TIME_MS, TimeUnit.MILLISECONDS);
        }

		throw new InvalidAuthenticationException(BaseResponseStatus.UNAUTHORIZED);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(AuthConst.PROXY_DELIVERY_IP);
        if (forwarded != null && !forwarded.isEmpty()) {
            int commaIndex = forwarded.indexOf(',');
            return (commaIndex > 0) ? forwarded.substring(0, commaIndex).trim() : forwarded.trim();
        }
        return request.getRemoteAddr();
    }
}