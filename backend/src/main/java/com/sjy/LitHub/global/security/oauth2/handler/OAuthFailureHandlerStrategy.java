package com.sjy.LitHub.global.security.oauth2.handler;

import com.sjy.LitHub.global.model.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface OAuthFailureHandlerStrategy {
    boolean supports(BaseResponseStatus status);
    void handle(HttpServletRequest request, HttpServletResponse response, BaseResponseStatus status) throws IOException;
}