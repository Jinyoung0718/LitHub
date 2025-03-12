package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.Getter;

@Getter
public class InvalidTokenException extends InvalidCustomException {

    public InvalidTokenException(BaseResponseStatus status) {
        super(status);
    }
}