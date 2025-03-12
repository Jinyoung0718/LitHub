package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;

public class InvalidFileException extends InvalidCustomException{

    public InvalidFileException(BaseResponseStatus status) {
        super(status);
    }
}