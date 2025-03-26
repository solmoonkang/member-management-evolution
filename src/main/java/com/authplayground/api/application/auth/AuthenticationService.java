package com.authplayground.api.application.auth;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.auth.CustomUserDetails;
import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.api.dto.member.request.LoginRequest;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional
	public void loginMember(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
		final Member member = memberRepository.findMemberByEmail(loginRequest.email())
			.orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND_FAILURE));

		validatePasswordMatches(loginRequest.password(), member.getPassword());

		CustomUserDetails userDetails = new CustomUserDetails(member);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		httpServletRequest.getSession(true).setAttribute(
			HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
	}

	private void validatePasswordMatches(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(PASSWORD_MISMATCH_FAILURE);
		}
	}
}
