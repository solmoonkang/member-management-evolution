package com.authplayground.global.auth.filter;

import static com.authplayground.global.common.util.AuthConstant.*;
import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.global.error.model.ErrorMessage.*;

import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.error.exception.UnauthorizedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Set<String> PERMIT_ALL_PATH_SET = Set.of(PUBLIC_API_PATHS);

	private final JwtProvider jwtProvider;
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	protected void doFilterInternal(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		FilterChain filterChain) {

		final String permitURI = httpServletRequest.getRequestURI();

		try {
			if (PERMIT_ALL_PATH_SET.contains(permitURI)) {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				return;
			}

			final String accessToken = jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER);

			if (jwtProvider.validateToken(accessToken)) {
				setAuthenticationContext(accessToken);
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				return;
			}

			throw new UnauthorizedException(UNAUTHORIZED_REQUEST);
		} catch (Exception e) {
			log.warn("[✅ LOGGER: JWT AUTHENTICATION FILTER] 인증 실패 또는 토큰이 존재하지 않습니다: {}", e.getMessage());
			handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
		}
	}

	private void setAuthenticationContext(String accessToken) {
		final AuthMember authMember = jwtProvider.extractAuthMemberFromToken(accessToken);
		final List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authMember.role().name()));
		final Authentication authentication = new UsernamePasswordAuthenticationToken(authMember, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
