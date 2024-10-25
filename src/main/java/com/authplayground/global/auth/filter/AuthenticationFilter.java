package com.authplayground.global.auth.filter;

import static com.authplayground.global.util.GlobalConstant.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.application.auth.JwtProviderService;
import com.authplayground.api.domain.auth.AuthMember;
import com.authplayground.global.error.exception.NotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

	protected final JwtProviderService jwtProviderService;
	protected final HandlerExceptionResolver handlerExceptionResolver;

	public AuthenticationFilter(
		JwtProviderService jwtProviderService,
		HandlerExceptionResolver handlerExceptionResolver) {
		this.jwtProviderService = jwtProviderService;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(
		@NotNull HttpServletRequest httpServletRequest,
		@NotNull HttpServletResponse httpServletResponse,
		@NotNull FilterChain filterChain) {
		final String accessToken = jwtProviderService.extractToken(ACCESS_TOKEN_HEADER, httpServletRequest);

		try {
			if (jwtProviderService.isUsable(accessToken)) {
				setAuthenticate(accessToken);
				filterChain.doFilter(httpServletRequest, httpServletResponse);

				return;
			}

			throw new NotFoundException("[❎ ERROR] JWT 토큰이 존재하지 않습니다.");
		} catch (Exception exception) {
			log.warn("[✅ LOGGER] JWT 에러 상세 설명: {}", exception.getMessage());
			handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, exception);
		}
	}

	protected void setAuthenticate(String accessToken) {
		final AuthMember authMember = jwtProviderService.extractAuthMemberByAccessToken(accessToken);
		final Authentication authentication = new UsernamePasswordAuthenticationToken(authMember, BLANK);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
