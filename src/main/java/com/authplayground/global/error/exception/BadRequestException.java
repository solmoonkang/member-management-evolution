package com.authplayground.global.error.exception;

import com.authplayground.global.error.model.ErrorMessage;

public class BadRequestException extends AuthPlaygroundException {

	public BadRequestException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}
}
