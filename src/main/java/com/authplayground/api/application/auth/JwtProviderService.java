package com.authplayground.api.application.auth;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static com.authplayground.global.util.GlobalConstant.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.domain.auth.AuthMember;
import com.authplayground.api.domain.auth.repository.TokenRepository;
import com.authplayground.api.dto.response.LoginResponse;
import com.authplayground.api.dto.response.TokenResponse;
import com.authplayground.global.config.TokenConfig;
import com.authplayground.global.error.exception.NotFoundException;
import com.authplayground.global.util.CookieUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtProviderService {

	private final TokenConfig tokenConfig;
	private final SecretKey secretKey;
	private final TokenRepository tokenRepository;

	public JwtProviderService(TokenConfig tokenConfig, TokenRepository tokenRepository) {
		this.tokenConfig = tokenConfig;
		byte[] secretKeyBytes = Decoders.BASE64.decode(tokenConfig.getSecretAccessKey());

		this.secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
		this.tokenRepository = tokenRepository;
	}

	public String generateAccessToken(String email, String nickname) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date();

		return buildJwt(issuedDate, expiredDate)
			.claim(MEMBER_EMAIL, email)
			.claim(MEMBER_NICKNAME, nickname)
			.compact();
	}

	public String generateRefreshToken(String email) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + tokenConfig.getRefreshTokenExpire());

		return buildJwt(issuedDate, expiredDate)
			.claim(MEMBER_EMAIL, email)
			.compact();
	}

	@Transactional
	public String reGenerateToken(String refreshToken, HttpServletResponse httpServletResponse) {
		final Claims claims = parseClaimsByToken(refreshToken);
		final String memberEmail = claims.get(MEMBER_EMAIL, String.class);
		final String memberNickname = claims.get(MEMBER_NICKNAME, String.class);

		LoginResponse loginResponse = tokenRepository.getTokenSaveValue(memberEmail);
		// 토큰 검증 로직

		validateRefreshToken(refreshToken, loginResponse.refreshToken());

		final String newAccessToken = generateAccessToken(memberEmail, memberNickname);
		final String newRefreshToken = generateRefreshToken(memberEmail);

		tokenRepository.saveToken(memberEmail, new TokenResponse(newRefreshToken));

		httpServletResponse.setHeader(ACCESS_TOKEN_HEADER, newAccessToken);

		final Cookie refreshTokenCookie = CookieUtil.generateRefreshTokenCookie(REFRESH_TOKEN_COOKIE, newRefreshToken);
		httpServletResponse.addCookie(refreshTokenCookie);

		return newAccessToken;
	}

	public String extractToken(String header, HttpServletRequest httpServletRequest) {
		final String token = httpServletRequest.getHeader(header);

		if (token == null || !token.startsWith(BEARER)) {
			log.warn("[✅ LOGGER] {}는 NULL이거나 BEARER가 아닙니다.", header);
			return null;
		}
		return token.replaceFirst(BEARER, "").trim();
	}

	public AuthMember extractAuthMemberByAccessToken(String accessToken) {
		final Claims claims = parseClaimsByToken(accessToken);
		final String memberEmail = claims.get(MEMBER_EMAIL, String.class);
		final String memberNickname = claims.get(MEMBER_NICKNAME, String.class);

		return AuthMember.createAuthMember(memberEmail, memberNickname);
	}

	public boolean isUsable(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);

			return true;
		} catch (ExpiredJwtException expiredJwtException) {
			log.warn("[✅ LOGGER] JWT 토큰이 만료되었습니다.");
		} catch (IllegalArgumentException illegalArgumentException) {
			log.warn("[✅ LOGGER] JWT 토큰이 존재하지 않습니다.");
			throw new NotFoundException(FAILED_TOKEN_NOT_FOUND);
		} catch (Exception exception) {
			log.warn("[✅ LOGGER] 유효하지 않은 토큰입니다.");
			throw new NotFoundException(FAILED_INVALID_TOKEN);
		}

		return false;
	}

	private JwtBuilder buildJwt(Date issuedDate, Date expiredDate) {
		return Jwts.builder()
			.issuer(tokenConfig.getIss())
			.issuedAt(issuedDate)
			.expiration(expiredDate)
			.signWith(secretKey);
	}

	private Claims parseClaimsByToken(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private void validateRefreshToken(String reGenerateRefreshToken, String savedRefreshToken) {
		if (!reGenerateRefreshToken.equals(savedRefreshToken)) {
			log.warn("[✅ LOGGER] 유효하지 않은 리프레시 토큰입니다.");
			throw new NotFoundException(FAILED_TOKEN_NOT_FOUND);
		}
	}
}
