package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;

public class InvalidRedisException extends InvalidCustomException{

	public InvalidRedisException(BaseResponseStatus status) {
		super(status);
	}
}