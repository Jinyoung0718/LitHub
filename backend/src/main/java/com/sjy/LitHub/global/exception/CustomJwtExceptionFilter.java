package com.sjy.LitHub.global.exception;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.sjy.LitHub.global.exception.custom.InvalidCustomException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomJwtExceptionFilter extends OncePerRequestFilter {

	private final HandlerExceptionResolver resolver;

	public CustomJwtExceptionFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (InvalidCustomException ex) {
			resolver.resolveException(request, response, null, ex);
		}
	}
}