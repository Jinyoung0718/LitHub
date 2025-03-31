package com.sjy.LitHub.global.security.oauth2.service.token;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.security.util.CookieUtil;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.AuthConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthTempTokenService {

    private final JwtUtil jwtUtil;
    private final TempTokenService tempTokenService;


    public Map<String, String> extractTokenData(HttpServletRequest request) {
        String tempToken = CookieUtil.getCookieValue(request, AuthConst.TOKEN_TYPE_TEMP);
        if (tempToken == null || tempToken.isEmpty()) {
            throw new InvalidUserException(BaseResponseStatus.USER_TEMP_SESSION_EXPIRED);
        }

		return tempTokenService.validateTempSignupToken(tempToken);
    }

    public void generateAndSetTokens(HttpServletResponse response, User newUser) {
        String accessToken = createAccessToken(newUser);
        String refreshToken = createRefreshToken(newUser);

        response.addCookie(CookieUtil.createCookie(AuthConst.TOKEN_TYPE_ACCESS, accessToken, AuthConst.COOKIE_ACCESS_EXPIRATION));
        response.addCookie(CookieUtil.createCookie(AuthConst.TOKEN_TYPE_REFRESH, refreshToken, AuthConst.COOKIE_REFRESH_EXPIRATION));
    }

    private String createAccessToken(User user) {
        return jwtUtil.createJwt(
                AuthConst.TOKEN_TYPE_ACCESS,
                user.getId(),
                user.getRole(),
                AuthConst.ACCESS_EXPIRATION
        );
    }

    private String createRefreshToken(User user) {
        return jwtUtil.createJwt(
                AuthConst.TOKEN_TYPE_REFRESH,
                user.getId(),
                user.getRole(),
                AuthConst.REFRESH_EXPIRATION
        );
    }
}