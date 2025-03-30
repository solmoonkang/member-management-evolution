package com.authplayground.global.auth.filter;

import static com.authplayground.global.common.util.JwtConstant.*;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.global.auth.token.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		FilterChain filterChain) {

		try {
			final String accessToken = jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER);

			if (jwtProvider.validateToken(accessToken)) {
				setAuthenticationContext(accessToken);
				log.debug("[✅ LOGGER: JWT AUTHENTICATION FILTER] 인증 객체 등록이 완료되었습니다.");
			}

			filterChain.doFilter(httpServletRequest, httpServletResponse);
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
