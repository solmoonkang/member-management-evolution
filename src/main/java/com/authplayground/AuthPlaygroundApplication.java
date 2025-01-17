package com.authplayground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.authplayground.global.config.TokenConfig;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(TokenConfig.class)
public class AuthPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthPlaygroundApplication.class, args);
	}

}
