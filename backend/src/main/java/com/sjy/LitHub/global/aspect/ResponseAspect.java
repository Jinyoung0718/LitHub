package com.sjy.LitHub.global.aspect;

import com.sjy.LitHub.global.model.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseAspect {

    private final HttpServletResponse response;

    @Around(
            """
            (
                within(@org.springframework.web.bind.annotation.RestController *)
                &&
                (
                    @annotation(org.springframework.web.bind.annotation.GetMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PostMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PutMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.DeleteMapping)
                )
            )
            ||
            @annotation(org.springframework.web.bind.annotation.ResponseBody)
            """
    )
    public Object applyHttpStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof BaseResponse<?> baseResponse) {
            response.setStatus(baseResponse.getHttpStatus());
        }

        return result;
    }
}