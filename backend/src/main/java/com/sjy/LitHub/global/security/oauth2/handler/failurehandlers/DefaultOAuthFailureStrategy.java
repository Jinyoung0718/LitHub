package com.sjy.LitHub.global.security.oauth2.handler.failurehandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.oauth2.handler.OAuthFailureHandlerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class DefaultOAuthFailureStrategy implements OAuthFailureHandlerStrategy {
    @Value("${url.base}")
    private String REDIRECT_URL;

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(BaseResponseStatus status) {
        return true;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BaseResponseStatus status) throws IOException {
        BaseResponseStatus responseStatus = (status != null) ? status : BaseResponseStatus.UNAUTHORIZED;

        response.setStatus(responseStatus.getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), BaseResponse.error(responseStatus, responseStatus.getMessage()));

        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL + "/login")
                .queryParam("error", responseStatus.getMessage())
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}