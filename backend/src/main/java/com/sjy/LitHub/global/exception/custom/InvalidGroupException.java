package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;

public class InvalidGroupException extends InvalidCustomException{

	public InvalidGroupException(BaseResponseStatus status) {
		super(status);
	}
}