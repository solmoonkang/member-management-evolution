package com.authplayground.global.common.util;

import org.springframework.stereotype.Component;

import com.authplayground.api.domain.member.model.AuthMember;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionManager {

	private static final String LOGIN_MEMBER = "LOGIN_MEMBER";

	public void saveAuthMember(HttpServletRequest httpServletRequest, AuthMember authMember) {
		httpServletRequest.getSession().setAttribute(LOGIN_MEMBER, authMember);
	}

	public AuthMember getAuthMember(HttpServletRequest httpServletRequest) {
		return (AuthMember)httpServletRequest.getSession().getAttribute(LOGIN_MEMBER);
	}

	public void expiredSession(HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession(false);
		invalidateSession(httpSession);
	}

	private void invalidateSession(HttpSession httpSession) {
		if (httpSession != null) {
			httpSession.invalidate();
		}
	}
}
