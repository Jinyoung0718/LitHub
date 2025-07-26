package com.sjy.LitHub.global.exception;

import com.sjy.LitHub.global.exception.custom.InvalidCustomException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final HandlerExceptionResolver resolver;

    public CustomAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        resolver.resolveException(request, response, null, new InvalidCustomException(BaseResponseStatus.ACCESS_DENIED));
    }
}