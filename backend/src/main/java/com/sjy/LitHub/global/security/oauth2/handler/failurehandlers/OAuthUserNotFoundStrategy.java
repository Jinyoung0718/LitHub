package com.sjy.LitHub.global.security.oauth2.handler.failurehandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.oauth2.handler.OAuthFailureHandlerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuthUserNotFoundStrategy implements OAuthFailureHandlerStrategy {
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(BaseResponseStatus status) {
        return status == BaseResponseStatus.OAUTH_USER_NOT_FOUND;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BaseResponseStatus status) throws IOException {
        response.setStatus(status.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), BaseResponse.error(status));
    }
}