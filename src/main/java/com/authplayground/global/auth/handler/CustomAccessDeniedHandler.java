package com.authplayground.global.auth.handler;

import static com.authplayground.global.error.model.ErrorMessage.*;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.authplayground.global.error.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		AccessDeniedException accessDeniedException) throws IOException {

		log.warn("[✅ LOGGER] 권한이 없는 사용자가 접근했습니다: {}", accessDeniedException.getMessage());

		httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
		httpServletResponse.setContentType("application/json;charset=UTF-8");

		ErrorResponse errorResponse = ErrorResponse.builder()
			.message(NO_PERMISSION_FAILURE.getMessage())
			.build();

		ObjectMapper objectMapper = new ObjectMapper();
		httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
