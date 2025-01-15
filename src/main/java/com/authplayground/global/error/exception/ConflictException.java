package com.authplayground.global.error.exception;

import com.authplayground.global.error.model.ErrorMessage;

public class ConflictException extends AuthPlaygroundException {

	public ConflictException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}
}
