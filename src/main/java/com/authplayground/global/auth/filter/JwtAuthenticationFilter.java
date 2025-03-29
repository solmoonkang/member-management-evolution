package com.authplayground.global.auth.filter;

import static com.authplayground.global.common.util.JwtConstant.*;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.global.auth.token.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			final String accessToken = jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER);

			if (jwtProvider.validateToken(accessToken)) {
				final AuthMember authMember = jwtProvider.extractAuthMemberFromToken(accessToken);
				final Authentication authentication = new UsernamePasswordAuthenticationToken(authMember, null,
					List.of(new SimpleGrantedAuthority("ROLE_" + authMember.role().name())));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.debug("[✅ LOGGER: JWT AUTHENTICATION FILTER] 인증 객체 등록이 완료되었습니다.");
			}
		} catch (Exception e) {
			log.warn("[✅ LOGGER: JWT AUTHENTICATION FILTER] 인증 실패 또는 토큰이 존재하지 않습니다: {}", e.getMessage());
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
