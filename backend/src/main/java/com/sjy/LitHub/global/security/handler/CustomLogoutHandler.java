package com.sjy.LitHub.global.security.handler;

import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.CookieUtil;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisBlacklistUtil;
import com.sjy.LitHub.global.security.util.RedisRefreshTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final RedisBlacklistUtil redisBlacklistUtil;
    private final RedisRefreshTokenUtil redisRefreshTokenUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String accessToken = CookieUtil.getCookieValue(request, AuthConst.TOKEN_TYPE_ACCESS);
        if (accessToken != null && !jwtUtil.isExpired(accessToken)) {
            long ttl = jwtUtil.getExpiration(accessToken);
            redisBlacklistUtil.addToBlacklist(accessToken, ttl); // 블랙리스트 추가
        }

        String refreshToken = CookieUtil.getCookieValue(request, AuthConst.TOKEN_TYPE_REFRESH);
        if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
            Long userId = jwtUtil.getUserId(refreshToken);
            redisRefreshTokenUtil.deleteRefreshToken(userId);
        }

        response.addCookie(CookieUtil.deleteCookie(AuthConst.TOKEN_TYPE_ACCESS));
        response.addCookie(CookieUtil.deleteCookie(AuthConst.TOKEN_TYPE_REFRESH));
    }
}