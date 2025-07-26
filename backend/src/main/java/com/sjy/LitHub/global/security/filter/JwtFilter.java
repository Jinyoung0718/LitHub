package com.sjy.LitHub.global.security.filter;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

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

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final RedisBlacklistUtil redisBlacklistUtil;
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/api/auth") ||
			requestURI.startsWith("/api/info/temp-check") ||
			requestURI.startsWith("/oauth2/authorization") ||
			requestURI.startsWith("/login/oauth2/code")
		) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = CookieUtil.getCookieValue(request, AuthConst.TOKEN_TYPE_ACCESS);

		if (accessToken == null || accessToken.isEmpty()) {
			throw new InvalidTokenException(BaseResponseStatus.JWT_MISSING);
		}

		if (redisBlacklistUtil.isInBlackList(accessToken)) {
			throw new InvalidTokenException(BaseResponseStatus.JWT_BLACKLISTED);
		}

		try {
			if (jwtUtil.isExpired(accessToken)) {
				String newAccessToken = handleExpiredToken(request, response);
				SecurityContextHolder.getContext().setAuthentication(tokenService.getAuthenticationFromToken(newAccessToken));
				filterChain.doFilter(request, response);
				return;
			}

			if (!tokenService.isAccessToken(accessToken)) {
				throw new InvalidTokenException(BaseResponseStatus.JWT_INVALID);
			}

			SecurityContextHolder.getContext().setAuthentication(tokenService.getAuthenticationFromToken(accessToken));
		} catch (JwtException e) {
			throw new InvalidTokenException(BaseResponseStatus.JWT_INVALID);
		}

		filterChain.doFilter(request, response);
	}

	private String handleExpiredToken(HttpServletRequest request, HttpServletResponse response) {
		return tokenService.rotatingTokens(request, response);
	}
}