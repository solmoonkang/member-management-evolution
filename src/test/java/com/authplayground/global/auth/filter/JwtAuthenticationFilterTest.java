package com.authplayground.global.auth.filter;

import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.global.error.model.ErrorMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.auth.validator.TokenValidator;
import com.authplayground.global.error.exception.UnauthorizedException;
import com.authplayground.support.fixture.JwtFixture;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@DisplayName("JwtAuthenticationFilter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@InjectMocks
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	JwtProvider jwtProvider;

	@Mock
	TokenValidator tokenValidator;

	@Mock
	HandlerExceptionResolver handlerExceptionResolver;

	@Mock
	HttpServletRequest httpServletRequest;

	@Mock
	HttpServletResponse httpServletResponse;

	@Mock
	FilterChain filterChain;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("[✅ SUCCESS] doFilterInternal - 인증된 요청은 SecurityContext에 Authentication을 설정합니다.")
	void doFilterInternal_returnsAuthenticationInSecurityContext_success() throws ServletException, IOException {
		// GIVEN
		AuthMember authMember = JwtFixture.createAuthMember();

		String validAccessToken = jwtProvider.generateAccessToken(
			authMember.email(), authMember.nickname(), authMember.role());

		when(httpServletRequest.getRequestURI()).thenReturn("/api/protected");
		when(jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER)).thenReturn(validAccessToken);
		when(jwtProvider.validateToken(validAccessToken)).thenReturn(true);
		when(jwtProvider.extractAuthMemberFromToken(validAccessToken)).thenReturn(authMember);
		doNothing().when(tokenValidator).validateTokenNotBlacklisted(validAccessToken);

		// WHEN
		jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		// THEN
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertThat(authentication).isNotNull();
		assertThat(authentication.getPrincipal()).isInstanceOf(AuthMember.class);
		assertThat(((AuthMember)authentication.getPrincipal()).email()).isEqualTo(authMember.email());
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	@DisplayName("[❎ FAILURE] doFilterInternal - 토큰이 없거나 잘못된 경우 인증 예외를 발생시킵니다.")
	void doFilterInternal_throwsUnauthorizedException_whenTokenMissingOrInvalid_failure() {
		// GIVEN
		when(httpServletRequest.getRequestURI()).thenReturn("/api/protected");
		when(jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER))
			.thenThrow(new UnauthorizedException(INVALID_AUTHORIZATION_HEADER));

		// WHEN
		jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

		// THEN
		verify(handlerExceptionResolver).resolveException(
			eq(httpServletRequest), eq(httpServletResponse), isNull(), any(UnauthorizedException.class));
	}
}
