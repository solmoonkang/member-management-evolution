package com.authplayground.global.auth;

import static com.authplayground.global.error.model.ErrorMessage.*;

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.global.auth.annotation.Auth;
import com.authplayground.global.common.util.SessionManager;
import com.authplayground.global.error.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

	private final SessionManager sessionManager;

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.hasParameterAnnotation(Auth.class)
			&& methodParameter.getParameterType().equals(AuthMember.class);
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter,
		ModelAndViewContainer modelAndViewContainer,
		NativeWebRequest nativeWebRequest,
		WebDataBinderFactory webDataBinderFactory) {

		HttpServletRequest httpServletRequest = (HttpServletRequest)nativeWebRequest.getNativeRequest();

		return resolveAuthMember(httpServletRequest)
			.orElseThrow(() -> new UnauthorizedException(UNAUTHORIZED_REQUEST));
	}

	private Optional<AuthMember> resolveAuthMember(HttpServletRequest httpServletRequest) {
		return getSessionAuthMember(httpServletRequest)
			.or(this::getJwtAuthMemberFromSecurityContext);
	}

	private Optional<AuthMember> getSessionAuthMember(HttpServletRequest httpServletRequest) {
		AuthMember sessionMember = sessionManager.getAuthMember(httpServletRequest);
		return Optional.ofNullable(sessionMember);
	}

	private Optional<AuthMember> getJwtAuthMemberFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof AuthMember authMember) {
			return Optional.of(authMember);
		}

		return Optional.empty();
	}
}
