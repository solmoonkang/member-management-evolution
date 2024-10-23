package com.authplayground.api.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.authplayground.api.dto.member.LoginRequest;
import com.authplayground.support.MemberFixture;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@InjectMocks
	AuthenticationService authenticationService;

	@Mock
	AuthenticationManager authenticationManager;

	@Test
	@DisplayName("AUTHENTICATION MEMBER (⭕️ SUCCESS): 해당 사용자는 성공적으로 인증이 완료되었습니다.")
	void authenticationMember_void_success() {
		// GIVEN
		LoginRequest loginRequest = MemberFixture.loginMemberRequest();
		Authentication authentication = new UsernamePasswordAuthenticationToken("test@gamil.com", "12345");

		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willReturn(authentication);

		// WHEN
		authenticationService.authenticationMember(loginRequest);

		// THEN
		Authentication actualAuthentication = SecurityContextHolder.getContext().getAuthentication();
		assertThat(actualAuthentication).isEqualTo(authentication);
	}

	@Test
	@DisplayName("AUTHENTICATION MEMBER (❌ FAILURE): 해당 사용자는 인증에 실패했습니다.")
	void authenticationMember_BadCredentialsException_fail() {
		// GIVEN
		LoginRequest loginRequest = MemberFixture.loginMemberRequestWithDifferentPassword();

		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willThrow(new BadCredentialsException("[❎ ERROR] 해당 사용자는 유효하지 않은 사용자입니다."));

		// WHEN & THEN
		assertThatThrownBy(() -> authenticationService.authenticationMember(loginRequest))
			.isInstanceOf(BadCredentialsException.class)
			.hasMessage("[❎ ERROR] 해당 사용자는 유효하지 않은 사용자입니다.");
	}
}
