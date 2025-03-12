package com.sjy.LitHub.global.security.oauth2.service;

import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TempTokenService {

    private final JwtUtil jwtUtil;

    public String createTempSignupToken(String email, String provider, String providerId, long expiredMs) {
        Map<String, Object> claims = Map.of(
                AuthConst.TOKEN_TYPE_CATEGORY, AuthConst.TOKEN_TYPE_TEMP,
                AuthConst.TEMP_USER_EMAIL, email,
                AuthConst.TEMP_PROVIDER, provider,
                AuthConst.TEMP_PROVIDER_ID, providerId
        );

        return jwtUtil.createJwt(claims, expiredMs);
    }

    public Map<String, String> validateTempSignupToken(String token) {
        try {
            Map<String, Object> payload = jwtUtil.getPayload(token);
            if (payload == null) {
                throw new InvalidUserException(BaseResponseStatus.TEMP_AUTHORIZATION_HEADER_INVALID);
            }

            String category = (String) payload.get(AuthConst.TOKEN_TYPE_CATEGORY);
            if (!AuthConst.TOKEN_TYPE_TEMP.equals(category)) {
                throw new InvalidUserException(BaseResponseStatus.TEMP_AUTHORIZATION_HEADER_INVALID);
            }

            return Map.of(
                    AuthConst.TEMP_USER_EMAIL, (String) payload.get(AuthConst.TEMP_USER_EMAIL),
                    AuthConst.TEMP_PROVIDER, (String) payload.get(AuthConst.TEMP_PROVIDER),
                    AuthConst.TEMP_PROVIDER_ID, (String) payload.get(AuthConst.TEMP_PROVIDER_ID)
            );

        } catch (ExpiredJwtException e) {
            throw new InvalidUserException(BaseResponseStatus.TEMP_AUTHORIZATION_HEADER_EXPIRED);
        } catch (Exception e) {
            throw new InvalidUserException(BaseResponseStatus.TEMP_AUTHORIZATION_HEADER_INVALID);
        }
    }
}