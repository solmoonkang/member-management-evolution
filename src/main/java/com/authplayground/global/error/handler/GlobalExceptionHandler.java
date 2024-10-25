package com.authplayground.global.error.handler;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.authplayground.global.error.exception.AuthPlaygroundException;
import com.authplayground.global.error.model.ErrorResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	protected ErrorResponse handleException() {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 알 수 없는 에러");

		return new ErrorResponse(FAILED_UNKNOWN_ERROR.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(AuthPlaygroundException.class)
	protected ErrorResponse handleAuthPlaygroundException(AuthPlaygroundException authPlaygroundException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 서버 에러");

		return new ErrorResponse(authPlaygroundException.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(Exception.class)
	protected ErrorResponse handleConflictException(AuthPlaygroundException authPlaygroundException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 충돌 에러");

		return new ErrorResponse(authPlaygroundException.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(Exception.class)
	protected ErrorResponse handleNotFoundException(AuthPlaygroundException authPlaygroundException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 리소스를 찾을 수 없는 에러");

		return new ErrorResponse(authPlaygroundException.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	protected ErrorResponse handleException(AuthPlaygroundException authPlaygroundException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 잘못된 요청 에러");

		return new ErrorResponse(authPlaygroundException.getMessage(), null);
	}
}
