package com.authplayground.global.auth.filter;

import static com.authplayground.global.common.util.CookieUtil.*;
import static com.authplayground.global.common.util.GlobalConstant.*;
import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.application.auth.JwtProviderService;
import com.authplayground.api.domain.auth.AuthMember;
import com.authplayground.global.auth.AuthenticationThreadLocal;
import com.authplayground.global.error.exception.NotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	protected final JwtProviderService jwtProviderService;
	protected final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	protected void doFilterInternal(
		@NotNull HttpServletRequest httpServletRequest,
		@NotNull HttpServletResponse httpServletResponse,
		@NotNull FilterChain filterChain) {
		String accessToken = jwtProviderService.extractToken(ACCESS_TOKEN_HEADER, httpServletRequest);
		String refreshToken = extractRefreshTokenFromCookies(httpServletRequest);
		String requestURI = httpServletRequest.getRequestURI();

		try {
			if (!jwtProviderService.isAuthenticationRequired(requestURI)) {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				return;
			}

			if (jwtProviderService.isUsable(accessToken)) {
				setAuthenticate(accessToken);
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				return;
			}

			if (jwtProviderService.isUsable(refreshToken)) {
				accessToken = jwtProviderService.reGenerateToken(refreshToken, httpServletResponse);
				setAuthenticate(accessToken);
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				return;
			}

			throw new NotFoundException(FAILED_TOKEN_NOT_FOUND);
		} catch (Exception exception) {
			log.warn("[✅ LOGGER] JWT 에러 상세 설명: {}", exception.getMessage());
			handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, exception);
		} finally {
			AuthenticationThreadLocal.removeAuthMemberHolder();
		}
	}

	protected void setAuthenticate(String accessToken) {
		final AuthMember authMember = jwtProviderService.extractAuthMemberByAccessToken(accessToken);
		final Authentication authentication = new UsernamePasswordAuthenticationToken(authMember, BLANK);
		AuthenticationThreadLocal.setAuthMemberHolder(authMember);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
