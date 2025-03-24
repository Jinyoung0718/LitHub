package com.sjy.LitHub.global.security.filter;

import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.exception.custom.InvalidTokenException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.service.TokenService;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.CookieUtil;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisBlacklistUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final RedisBlacklistUtil redisBlacklistUtil;
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/api/auth") ||
			requestURI.startsWith("/api/info/temp-check") ||
			requestURI.startsWith("/oauth2/authorization") ||
			requestURI.startsWith("/login/oauth2/code")) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = CookieUtil.getCookieValue(request, AuthConst.TOKEN_TYPE_ACCESS);
		log.debug("Access Token: {}", accessToken);

		if (accessToken == null || accessToken.isEmpty()) {
			setException(request, response, new InvalidTokenException(BaseResponseStatus.JWT_MISSING));
			return;
		}

		if (redisBlacklistUtil.isInBlackList(accessToken)) {
			setException(request, response, new InvalidTokenException(BaseResponseStatus.JWT_BLACKLISTED));
			return;
		}

		try {
			if (jwtUtil.isExpired(accessToken)) {
				String newAccessToken = handleExpiredToken(request, response);
				SecurityContextHolder.getContext().setAuthentication(tokenService.getAuthenticationFromToken(newAccessToken));
				filterChain.doFilter(request, response);
				return;
			}

			if (!tokenService.isAccessToken(accessToken)) {
				setException(request, response, new InvalidTokenException(BaseResponseStatus.JWT_INVALID));
				return;
			}

			SecurityContextHolder.getContext().setAuthentication(tokenService.getAuthenticationFromToken(accessToken));
		} catch (JwtException e) {
			setException(request, response, new InvalidTokenException(BaseResponseStatus.JWT_INVALID));
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String handleExpiredToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			return tokenService.rotatingTokens(request, response);
		} catch (InvalidAuthenticationException e) {
			setException(request, response, e);
			return null;
		}
	}

	private void setException(HttpServletRequest request, HttpServletResponse response, RuntimeException exception) {
		SecurityContextHolder.clearContext();
		request.setAttribute("exception", exception);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}