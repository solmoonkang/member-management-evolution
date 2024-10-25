package com.authplayground.api.application.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.authplayground.api.domain.auth.AuthMember;
import com.authplayground.api.domain.member.repository.MemberRepository;
import com.authplayground.global.config.TokenConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtProviderService {

	private final TokenConfig tokenConfig;
	private final SecretKey secretKey;
	private final MemberRepository memberRepository;

	private static final String MEMBER_EMAIL = "email";
	private static final String MEMBER_NICKNAME = "nickname";

	public JwtProviderService(TokenConfig tokenConfig, MemberRepository memberRepository) {
		this.tokenConfig = tokenConfig;
		byte[] secretKeyBytes = Decoders.BASE64.decode(tokenConfig.getSecretAccessKey());

		this.secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
		this.memberRepository = memberRepository;
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
		} catch (Exception exception) {
			log.warn("[✅ LOGGER] 유효하지 않은 토큰입니다.");
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
}
