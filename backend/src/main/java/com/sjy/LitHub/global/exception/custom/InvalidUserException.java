package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;

public class InvalidUserException extends InvalidCustomException{
    public InvalidUserException(BaseResponseStatus status) {
        super(status);
    }
}