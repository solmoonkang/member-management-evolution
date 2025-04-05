package com.authplayground.global.auth.token;

import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.support.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.security.Key;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.authplayground.api.domain.member.model.AuthMember;
import com.authplayground.api.domain.member.model.Role;
import com.authplayground.global.auth.token.config.JwtProviderTestConfig;
import com.authplayground.global.error.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@DisplayName("JwtProvider 단위 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JwtProviderTestConfig.class)
@TestPropertySource(properties = {
	"jwt.secret-key=test-secret-key-test-secret-key-test-secret-key",
	"jwt.issuer=test-issuer",
	"jwt.access-token-expiration=300000",
	"jwt.refresh-token-expiration=1209600000"
})
class JwtProviderTest {

	@Autowired
	JwtProvider jwtProvider;

	@Test
	@DisplayName("[✅ SUCCESS] generateAccessToken - 인증 정보를 포함한 토큰 생성 후 유효성을 확인합니다.")
	void generateAccessToken_returnsToken_success() {
		// WHEN
		String accessToken = jwtProvider.generateAccessToken(EMAIL, NICKNAME, Role.MEMBER);
		AuthMember authMember = jwtProvider.extractAuthMemberFromToken(accessToken);

		// THEN
		assertThat(jwtProvider.validateToken(accessToken)).isTrue();
		assertThat(authMember.email()).isEqualTo(EMAIL);
		assertThat(authMember.nickname()).isEqualTo(NICKNAME);
		assertThat(authMember.role()).isEqualTo(Role.MEMBER);

		Key signingKey = (Key)ReflectionTestUtils.getField(jwtProvider, "signingKey");

		Jws<Claims> claims = Jwts.parserBuilder()
			.setSigningKey(signingKey)
			.build()
			.parseClaimsJws(accessToken);

		assertThat(claims.getBody().get(CLAIM_EMAIL, String.class)).isEqualTo(EMAIL);
		assertThat(claims.getBody().get(CLAIM_NICKNAME, String.class)).isEqualTo(NICKNAME);
		assertThat(claims.getBody().get(CLAIM_ROLE, String.class)).isEqualTo(Role.MEMBER.name());
	}

	@Test
	@DisplayName("[✅ SUCCESS] generateRefreshToken - 이메일만 포함된 토큰을 생성하고 Claims를 확인합니다.")
	void generateRefreshToken_returnsToken_success() {
		// WHEN
		String refreshToken = jwtProvider.generateRefreshToken(EMAIL);

		// THEN
		assertThat(jwtProvider.validateToken(refreshToken)).isTrue();

		Key signingKey = (Key)ReflectionTestUtils.getField(jwtProvider, "signingKey");

		Jws<Claims> claims = Jwts.parserBuilder()
			.setSigningKey(signingKey)
			.build()
			.parseClaimsJws(refreshToken);

		assertThat(claims.getBody().get(CLAIM_EMAIL, String.class)).isEqualTo(EMAIL);
		assertThat(claims.getBody().get(CLAIM_NICKNAME)).isNull();
		assertThat(claims.getBody().get(CLAIM_ROLE)).isNull();
	}

	@Nested
	@DisplayName("extractAuthMemberFromToken() 테스트: ")
	class ExtractAuthMemberFromToken {

		@Test
		@DisplayName("[✅ SUCCESS] extractAuthMemberFromToken - 액세스 토큰에서 인증 정보를 추출합니다.")
		void extractAuthMemberFromToken_returnsAuthMember_success() {
			// GIVEN
			String accessToken = jwtProvider.generateAccessToken(EMAIL, NICKNAME, Role.MEMBER);

			// WHEN
			AuthMember authMember = jwtProvider.extractAuthMemberFromToken(accessToken);

			// THEN
			assertThat(authMember.email()).isEqualTo(EMAIL);
			assertThat(authMember.nickname()).isEqualTo(NICKNAME);
			assertThat(authMember.role()).isEqualTo(Role.MEMBER);
		}

		@Test
		@DisplayName("[❎ FAILURE] extractAuthMemberFromToken - 리프레시 토큰은 권한 정보가 없어 실패합니다.")
		void extractAuthMemberFromToken_throwsNullPointerException_whenRefreshTokenDoesNotHaveRole_failure() {
			// GIVEN
			String refreshToken = jwtProvider.generateRefreshToken(EMAIL);

			// WHEN & THEN
			assertThatThrownBy(() -> jwtProvider.extractAuthMemberFromToken(refreshToken))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("Name is null");
		}
	}

	@Nested
	@DisplayName("extractToken() 테스트: ")
	class ExtractToken {

		@Test
		@DisplayName("[✅ SUCCESS] extractToken - Bearer 접두사가 있는 헤더에서 토큰을 추출합니다.")
		void extractToken_returnsToken_success() {
			// GIVEN
			String validToken = "valid-token";
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
			httpServletRequest.addHeader(AUTHORIZATION_HEADER, BEARER_TYPE + validToken);

			// WHEN
			String actualToken = jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER);

			// THEN
			assertThat(actualToken).isEqualTo(validToken);
		}

		@Test
		@DisplayName("[❎ FAILURE] extractToken - 헤더가 없으면 예외가 발생합니다.")
		void extractToken_throwsUnauthorizedException_whenHeaderIsMissing_failure() {
			// GIVEN
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

			// WHEN & THEN
			assertThatThrownBy(() -> jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다.");
		}

		@Test
		@DisplayName("[❎ FAILURE] extractToken - Bearer 접두사가 없으면 예외가 발생합니다.")
		void extractToken_throwsUnauthorizedException_whenBearerPrefixMissing_failure() {
			// GIVEN
			String invalidToken = "TokenWithoutBearer";
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
			httpServletRequest.addHeader(AUTHORIZATION_HEADER, invalidToken);

			// WHEN & THEN
			assertThatThrownBy(() -> jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다.");
		}
	}

	@Nested
	@DisplayName("validateToken() 테스트")
	class ValidateToken {

		@Test
		@DisplayName("[✅ SUCCESS] validateToken - 유효한 토큰이면 true를 반환합니다.")
		void validateToken_returnsTrue_success() {
			// GIVEN
			String token = jwtProvider.generateAccessToken(EMAIL, NICKNAME, Role.MEMBER);

			// WHEN
			boolean isValid = jwtProvider.validateToken(token);

			// THEN
			assertThat(isValid).isTrue();
		}

		@Test
		@DisplayName("[❎ FAILURE] validateToken - 서명이 잘못된 토큰은 false를 반환합니다.")
		void validateToken_throwsUnauthorizedException_whenSignatureInvalid_failure() {
			// GIVEN
			String token = Jwts.builder()
				.setSubject("invalid")
				.setExpiration(new Date(System.currentTimeMillis() + 60000))
				.signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
				.compact();

			// WHEN & THEN
			assertThatThrownBy(() -> jwtProvider.validateToken(token))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다.");
		}

		@Test
		@DisplayName("[❎ FAILURE] validateToken - 만료된 토큰은 false를 반환합니다.")
		void validateToken_returnsFalse_whenTokenExpired_failure() {
			// GIVEN
			String token = Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() - 1000))
				.signWith((Key)ReflectionTestUtils.getField(jwtProvider, "signingKey"), SignatureAlgorithm.HS256)
				.compact();

			// WHEN
			boolean isValid = jwtProvider.validateToken(token);

			// THEN
			assertThat(isValid).isFalse();
		}

		@Test
		@DisplayName("[❎ FAILURE] validateToken - 구조가 잘못된 토큰은 false를 반환합니다.")
		void validateToken_returnsFalse_whenTokenMalformed_failure() {
			// GIVEN
			String token = "this.is.not.valid.jwt";

			// WHEN
			boolean isValid = jwtProvider.validateToken(token);

			// THEN
			assertThat(isValid).isFalse();
		}
	}

	@Nested
	@DisplayName("getTokenRemainingTime() 테스트")
	class GetTokenRemainingTime {

		@Test
		@DisplayName("[✅ SUCCESS] getTokenRemainingTime - 토큰의 만료 시간까지 남은 시간을 반환합니다.")
		void getTokenRemainingTime_returnsPositive_success() {
			// GIVEN
			String token = jwtProvider.generateAccessToken(EMAIL, NICKNAME, Role.MEMBER);

			// WHEN
			long remaining = jwtProvider.getTokenRemainingTime(token);

			// THEN
			assertThat(remaining).isPositive();
		}

		@Test
		@DisplayName("[❎ FAILURE] getTokenRemainingTime - 만료된 토큰일 경우 예외가 발생하지 않고 음수 반환됩니다.")
		void getTokenRemainingTime_returnsNegative_whenTokenExpired_failure() {
			// GIVEN
			String token = Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() - 1000))
				.signWith((Key)ReflectionTestUtils.getField(jwtProvider, "signingKey"), SignatureAlgorithm.HS256)
				.compact();

			// WHEN
			long remaining = jwtProvider.getTokenRemainingTime(token);

			// THEN
			assertThat(remaining).isNegative();
		}
	}
}
