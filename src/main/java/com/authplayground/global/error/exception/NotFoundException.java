package com.authplayground.global.error.exception;

import com.authplayground.global.error.model.ErrorMessage;

public class NotFoundException extends AuthPlaygroundException {

	public NotFoundException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}
}
