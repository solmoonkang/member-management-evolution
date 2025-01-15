package com.authplayground.global.error.handler;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.authplayground.global.error.exception.AuthPlaygroundException;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;
import com.authplayground.global.error.exception.NotFoundException;
import com.authplayground.global.error.exception.UnauthorizedException;
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
	@ExceptionHandler(ConflictException.class)
	protected ErrorResponse handleConflictException(ConflictException conflictException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 충돌 에러");

		return new ErrorResponse(conflictException.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	protected ErrorResponse handleNotFoundException(NotFoundException notFoundException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 리소스를 찾을 수 없는 에러");

		return new ErrorResponse(notFoundException.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	protected ErrorResponse handleException(BadRequestException badRequestException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 잘못된 요청 에러");

		return new ErrorResponse(badRequestException.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedException.class)
	protected ErrorResponse handleUnauthorizedException(UnauthorizedException unauthorizedException) {
		log.error("[✅ LOGGER] GLOBAL EXCEPTION HANDLER: 인증되지 않은 사용자 접근 에러");

		return new ErrorResponse(unauthorizedException.getMessage(), null);
	}
}
