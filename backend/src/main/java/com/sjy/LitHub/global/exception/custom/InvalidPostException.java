package com.sjy.LitHub.global.exception.custom;

import com.sjy.LitHub.global.model.BaseResponseStatus;

public class InvalidPostException extends InvalidCustomException {

	public InvalidPostException(BaseResponseStatus status) {super(status);}
}