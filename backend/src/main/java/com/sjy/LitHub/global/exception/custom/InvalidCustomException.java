package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.Getter;

@Getter
public class InvalidCustomException extends RuntimeException {
    private final BaseResponseStatus status;

    public InvalidCustomException(BaseResponseStatus status) {
        this.status = status;
    }
}