package com.sjy.LitHub.global.security.service;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.CookieUtil;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisRefreshTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RedisRefreshTokenUtil redisRefreshTokenUtil;

    public boolean isAccessToken(String token) {
        return "access".equals(jwtUtil.getCategory(token));
    }

    public Authentication getAuthenticationFromToken(String token) {
        Long userId = jwtUtil.getUserId(token);
        Role role = jwtUtil.getRole(token);
        UserPrincipal userPrincipal = new UserPrincipal(userId, role);
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    public String generateTokensAndSetCookies(HttpServletResponse response, Long userId, Role role) {
        String newAccessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, userId, role, AuthConst.ACCESS_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_REFRESH, userId, role, AuthConst.REFRESH_EXPIRATION);

        redisRefreshTokenUtil.deleteRefreshToken(userId);
        redisRefreshTokenUtil.addRefreshToken(userId, newRefreshToken, AuthConst.REFRESH_EXPIRATION);

        response.addCookie(CookieUtil.createCookie(AuthConst.TOKEN_TYPE_ACCESS, newAccessToken, AuthConst.COOKIE_ACCESS_EXPIRATION));
        response.addCookie(CookieUtil.createCookie(AuthConst.TOKEN_TYPE_REFRESH, newRefreshToken, AuthConst.COOKIE_REFRESH_EXPIRATION));

        SecurityContextHolder.getContext().setAuthentication(getAuthenticationFromToken(newAccessToken));
        return newAccessToken;
    }

    public String rotatingTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request, AuthConst.TOKEN_TYPE_REFRESH);

        if (refreshToken == null || refreshToken.isEmpty() || jwtUtil.isExpired(refreshToken)) {
            throw new InvalidAuthenticationException(BaseResponseStatus.REFRESH_TOKEN_EXPIRED);
        }

        RefreshTokenValidationStatus.validateToken(refreshToken, jwtUtil, redisRefreshTokenUtil);
        Long userId = jwtUtil.getUserId(refreshToken);
        Role role = jwtUtil.getRole(refreshToken);
        return generateTokensAndSetCookies(response, userId, role);
    }
}