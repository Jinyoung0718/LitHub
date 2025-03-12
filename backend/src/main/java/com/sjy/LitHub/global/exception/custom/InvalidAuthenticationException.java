package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.Getter;

@Getter
public class InvalidAuthenticationException extends InvalidCustomException  {

    public InvalidAuthenticationException(BaseResponseStatus status) {
        super(status);
    }
}