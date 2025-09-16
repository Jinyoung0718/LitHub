package com.sjy.LitHub.global.exception;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.sjy.LitHub.global.exception.custom.InvalidTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomTokenExceptionFilter extends OncePerRequestFilter {

	private final HandlerExceptionResolver resolver;

	public CustomTokenExceptionFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (InvalidTokenException ex) {
			// 필터 단계에서 발생한 예외를 강제로 DispatcherServlet 예외 처리 흐름에 태워주는 역할
			resolver.resolveException(request, response, null, ex);
		}
	}
}