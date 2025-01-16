package com.authplayground.global.config;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class TokenConfig {

	private final String iss;
	private final String secretAccessKey;
	private final long accessTokenExpire;
	private final long refreshTokenExpire;
	private final SecretKey secretKey;

	@ConstructorBinding
	public TokenConfig(String iss, String secretAccessKey, long accessTokenExpire, long refreshTokenExpire) {
		this.iss = iss;
		this.secretAccessKey = secretAccessKey;
		this.accessTokenExpire = accessTokenExpire;
		this.refreshTokenExpire = refreshTokenExpire;

		byte[] secretKeyBytes = Decoders.BASE64.decode(secretAccessKey);
		this.secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
	}
}
