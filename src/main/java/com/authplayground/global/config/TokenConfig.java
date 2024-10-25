package com.authplayground.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
@ConfigurationProperties(prefix = "jwt")
public class TokenConfig {

	private String iss;
	private String secretAccessKey;
	private long accessTokenExpire;
	private long refreshTokenExpire;
}
