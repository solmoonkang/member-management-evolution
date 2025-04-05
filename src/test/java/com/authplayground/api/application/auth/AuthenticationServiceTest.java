package com.authplayground.api.application.auth;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.authplayground.api.application.auth.validator.AuthenticationValidator;
import com.authplayground.api.application.member.MemberReadService;
import com.authplayground.api.domain.member.entity.Member;
import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.repository.BlacklistRepository;
import com.authplayground.api.domain.member.repository.TokenRepository;
import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.auth.response.LoginResponse;
import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.common.util.SessionManager;
import com.authplayground.global.error.exception.NotFoundException;
import com.authplayground.global.error.exception.UnauthorizedException;
import com.authplayground.support.JwtFixture;
import com.authplayground.support.MemberFixture;

import jakarta.servlet.http.HttpServletRequest;

@DisplayName("AuthenticationService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@InjectMocks
	AuthenticationService authenticationService;

	@Mock
	JwtProvider jwtProvider;

	@Mock
	SessionManager sessionManager;

	@Mock
	HttpServletRequest httpServletRequest;

	@Mock
	AuthenticationValidator authenticationValidator;

	@Mock
	AuthenticationTokenService authenticationTokenService;

	@Mock
	MemberReadService memberReadService;

	@Mock
	TokenRepository tokenRepository;

	@Mock
	BlacklistRepository blacklistRepository;

	@Nested
	@DisplayName("loginMember() 테스트: ")
	class LoginMember {

		@Test
		@DisplayName("[✅ SUCCESS] loginMember - 사용자가 올바른 정보로 로그인에 성공합니다.")
		void loginMember_returnsLoginResponse_success() {
			// GIVEN
			Member member = MemberFixture.createMember();
			LoginRequest loginRequest = MemberFixture.createLoginRequest();

			String accessToken = "accessToken";
			String refreshToken = "refreshToken";

			when(memberReadService.getMemberByEmail(member.getEmail())).thenReturn(member);
			doNothing().when(authenticationValidator).validatePasswordMatches(any(), any());
			when(jwtProvider.generateAccessToken(any(), any(), any())).thenReturn(accessToken);
			when(jwtProvider.generateRefreshToken(any())).thenReturn(refreshToken);

			// WHEN
			LoginResponse loginResponse = authenticationService.loginMember(loginRequest);

			// THEN
			assertThat(loginResponse.tokenResponse().accessToken()).isEqualTo(accessToken);
			assertThat(loginResponse.tokenResponse().refreshToken()).isEqualTo(refreshToken);
			assertThat(loginResponse.authMember().email()).isEqualTo(member.getEmail());

			verify(tokenRepository).saveToken(eq(member.getEmail()), eq(refreshToken));
		}

		@Test
		@DisplayName("[❎ FAILURE] loginMember - 존재하지 않는 이메일로 로그인을 요청했습니다.")
		void loginMember_throwsNotFoundException_whenEmailNotFound_failure() {
			// GIVEN
			LoginRequest loginRequest = MemberFixture.createWrongEmailLoginRequest();

			when(memberReadService.getMemberByEmail(any()))
				.thenThrow(new NotFoundException(MEMBER_NOT_FOUND_FAILURE));

			// WHEN & THEN
			assertThatThrownBy(() -> authenticationService.loginMember(loginRequest))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("[❎ ERROR] 요청하신 회원을 찾을 수 없습니다.");
		}

		@Test
		@DisplayName("[❎ FAILURE] loginMember - 잘못된 비밀번호로 로그인을 요청했습니다.")
		void loginMember_throwsBadRequestException_whenPasswordMismatch_failure() {
			// GIVEN
			Member member = MemberFixture.createMember();
			LoginRequest loginRequest = MemberFixture.createWrongPasswordLoginRequest();

			when(memberReadService.getMemberByEmail(member.getEmail())).thenReturn(member);
			doThrow(new UnauthorizedException(PASSWORD_MISMATCH_FAILURE))
				.when(authenticationValidator).validatePasswordMatches(any(), any());

			// WHEN & THEN
			assertThatThrownBy(() -> authenticationService.loginMember(loginRequest))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 입력하신 비밀번호가 일치하지 않습니다.");
		}
	}

	@Nested
	@DisplayName("logoutMember() 테스트: ")
	class LogoutMember {

		@Test
		@DisplayName("[✅ SUCCESS] logoutMember - 로그아웃 시 토큰 블랙리스트 등록 및 세션 종료를 정상 처리합니다.")
		void logoutMember_performsLogoutOperations_success() {
			// GIVEN
			AuthMember authMember = JwtFixture.createAuthMember();
			String accessToken = "accessToken";
			long remainingTime = 123456L;

			when(authenticationTokenService.extractAccessToken(httpServletRequest)).thenReturn(accessToken);
			when(authenticationTokenService.getRemainingAccessTokenTime(accessToken)).thenReturn(remainingTime);

			// WHEN
			authenticationService.logoutMember(authMember, httpServletRequest);

			// THEN
			verify(authenticationTokenService).extractAccessToken(httpServletRequest);
			verify(authenticationTokenService).getRemainingAccessTokenTime(accessToken);
			verify(blacklistRepository).registerBlacklist(accessToken, remainingTime);
			verify(tokenRepository).deleteTokenByEmail(authMember.email());
			verify(sessionManager).expiredSession(httpServletRequest);
		}

		@Test
		@DisplayName("[❎ FAILURE] logoutMember - 요청에서 액세스 토큰 추출에 실패했습니다.")
		void logoutMember_throwsUnauthorizedException_whenAccessTokenMissing_failure() {
			// GIVEN
			AuthMember authMember = JwtFixture.createAuthMember();

			when(authenticationTokenService.extractAccessToken(httpServletRequest))
				.thenThrow(new UnauthorizedException(INVALID_AUTHORIZATION_HEADER));

			// WHEN & THEN
			assertThatThrownBy(() -> authenticationService.logoutMember(authMember, httpServletRequest))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다.");
		}

		@Test
		@DisplayName("[❎ FAILURE] logoutMember - 토큰 삭제 중 예외 발생 시 예외가 전파됩니다.")
		void logoutMember_throwsRuntimeException_whenDeleteTokenFails_failure() {
			// GIVEN
			AuthMember authMember = JwtFixture.createAuthMember();
			String accessToken = "accessToken";
			long remainingTime = 123456L;

			when(authenticationTokenService.extractAccessToken(httpServletRequest)).thenReturn(accessToken);
			when(authenticationTokenService.getRemainingAccessTokenTime(accessToken)).thenReturn(remainingTime);
			doThrow(new RuntimeException("Delete failed")).when(tokenRepository).deleteTokenByEmail(authMember.email());

			// WHEN & THEN
			assertThatThrownBy(() -> authenticationService.logoutMember(authMember, httpServletRequest))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Delete failed");
		}
	}
}
