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
	@DisplayName("[✅ SUCCESS] 액세스 토큰을 생성하고 AuthMember를 통해 인증 정보를 추출할 수 있습니다.")
	void generateAccessToken_token_success() {
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
	@DisplayName("[✅ SUCCESS] 리프레시 토큰을 생성하고 이메일을 추출할 수 있습니다.")
	void generateRefreshToken_token_success() {
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
	@DisplayName("extractAuthMemberFromToken 메서드: ")
	class ExtractAuthMemberFromToken {

		@Test
		@DisplayName("[✅ SUCCESS] 액세스 토큰에서 AuthMember 정보를 정상적으로 추출할 수 있습니다.")
		void extractAuthMemberFromToken_authMember_success() {
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
		@DisplayName("[❎ FAILURE] 리프레시 토큰에서 권한(Role) 정보가 없어 파싱에 실패합니다.")
		void extractAuthMemberFromToken_roleNull_failure() {
			// GIVEN
			String refreshToken = jwtProvider.generateRefreshToken(EMAIL);

			// WHEN & THEN
			assertThatThrownBy(() -> jwtProvider.extractAuthMemberFromToken(refreshToken))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("Name is null");
		}
	}

	@Nested
	@DisplayName("extractToken 메서드: ")
	class ExtractToken {

		@Test
		@DisplayName("[✅ SUCCESS] Bearer 접두사를 포함한 헤더에서 토큰을 추출합니다.")
		void extractToken_success() {
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
		@DisplayName("[❎ FAILURE] 헤더에 토큰이 없으면 예외가 발생합니다.")
		void extractToken_missingHeader_failure() {
			// GIVEN
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

			// WHEN & THEN
			assertThatThrownBy(() -> jwtProvider.extractToken(httpServletRequest, AUTHORIZATION_HEADER))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다.");
		}

		@Test
		@DisplayName("[❎ FAILURE] Bearer 접두사가 없으면 예외가 발생합니다.")
		void extractToken_invalidPrefix_failure() {
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
	@DisplayName("validateToken 메서드: ")
	class ValidateToken {

		@Test
		@DisplayName("[✅ SUCCESS] 유효한 토큰이면 true를 반환합니다.")
		void validateToken_valid_success() {
			// GIVEN
			String token = jwtProvider.generateAccessToken(EMAIL, NICKNAME, Role.MEMBER);

			// WHEN

			// THEN
			assertThat(jwtProvider.validateToken(token)).isTrue();
		}

		@Test
		@DisplayName("[❎ FAILURE] 서명이 잘못된 토큰은 false를 반환합니다.")
		void validateToken_invalidSignature_failure() {
			// GIVEN
			String token = Jwts.builder()
				.setSubject("invalid")
				.setExpiration(new Date(System.currentTimeMillis() + 60000))
				.signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
				.compact();

			// WHEN

			// THEN
			assertThatThrownBy(() -> jwtProvider.validateToken(token))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("[❎ ERROR] 유효하지 않은 Authorization 헤더입니다.");
		}

		@Test
		@DisplayName("[❎ FAILURE] 만료된 토큰은 false를 반환합니다.")
		void validateToken_expired_failure() {
			String token = Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() - 1000))
				.signWith((Key)ReflectionTestUtils.getField(jwtProvider, "signingKey"), SignatureAlgorithm.HS256)
				.compact();

			assertThat(jwtProvider.validateToken(token)).isFalse();
		}

		@Test
		@DisplayName("[❎ FAILURE] 구조가 잘못된 토큰은 false를 반환합니다.")
		void validateToken_malformed_failure() {
			String token = "this.is.not.valid.jwt";

			assertThat(jwtProvider.validateToken(token)).isFalse();
		}
	}

	@Nested
	@DisplayName("getTokenRemainingTime 메서드: ")
	class GetTokenRemainingTime {

		@Test
		@DisplayName("[✅ SUCCESS] 토큰의 만료 시간까지 남은 시간을 반환합니다.")
		void getTokenRemainingTime_success() {
			String token = jwtProvider.generateAccessToken(EMAIL, NICKNAME, Role.MEMBER);
			long remaining = jwtProvider.getTokenRemainingTime(token);

			assertThat(remaining).isPositive();
		}

		@Test
		@DisplayName("[❎ FAILURE] 만료된 토큰일 경우 예외가 발생하지 않고 음수 반환됩니다.")
		void getTokenRemainingTime_expiredToken_returnsNegative() {
			String token = Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() - 1000))
				.signWith((Key)ReflectionTestUtils.getField(jwtProvider, "signingKey"), SignatureAlgorithm.HS256)
				.compact();

			long remaining = jwtProvider.getTokenRemainingTime(token);

			assertThat(remaining).isNegative();
		}
	}
}
