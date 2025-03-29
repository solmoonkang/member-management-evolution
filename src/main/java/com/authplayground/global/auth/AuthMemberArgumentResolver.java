package com.authplayground.global.auth;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.auth.CustomUserDetails;
import com.authplayground.global.auth.annotation.Auth;

@Component
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.hasParameterAnnotation(Auth.class)
			&& methodParameter.getParameterType().equals(AuthMember.class);
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
		NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
			authentication.getPrincipal().equals("anonymousUser")) {

			return null;
		}

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		return AuthMember.from(userDetails.member());
	}
}
