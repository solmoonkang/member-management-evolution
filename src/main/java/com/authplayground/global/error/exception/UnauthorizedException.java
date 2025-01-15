package com.authplayground.global.error.exception;

import com.authplayground.global.error.model.ErrorMessage;

public class UnauthorizedException extends AuthPlaygroundException {

	public UnauthorizedException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}
}
