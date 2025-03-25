package com.authplayground.global.config.security;

import static com.authplayground.global.error.model.ErrorMessage.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.authplayground.global.error.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		AuthenticationException authenticationException) throws IOException {

		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpServletResponse.setContentType("application/json;charset=UTF-8");

		ErrorResponse errorResponse = ErrorResponse.builder()
			.message(UNAUTHORIZED_REQUEST.getMessage())
			.build();

		ObjectMapper objectMapper = new ObjectMapper();
		httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
