package com.sjy.LitHub.global.exception;

import com.sjy.LitHub.global.exception.custom.InvalidCustomException;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.model.Empty;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCustomException.class)
    public BaseResponse<Empty> handleGlobalException(InvalidCustomException e, HttpServletResponse response) {
        response.setStatus(e.getStatus().getHttpStatus());
        return BaseResponse.error(e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return BaseResponse.error(BaseResponseStatus.VALIDATION_FAILED, errors);
    }
}