package com.authplayground.global.config;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.application.auth.JwtProviderService;
import com.authplayground.global.auth.filter.AuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtProviderService jwtProviderService;
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return webSecurityCustomizer -> webSecurityCustomizer.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.requestMatchers("/h2-console/**")
			.requestMatchers("/v3/api-docs/**")
			.requestMatchers("/swagger-ui/**")
			.requestMatchers("/swagger-ui/index.html");
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		httpSecurity.authorizeHttpRequests(auth -> auth
			.requestMatchers("/api/signup", "/api/login").permitAll()
			.anyRequest().authenticated());

		httpSecurity.addFilterBefore(
			new AuthenticationFilter(jwtProviderService, handlerExceptionResolver),
			UsernamePasswordAuthenticationFilter.class);

		httpSecurity.sessionManagement(session -> session
			.sessionCreationPolicy(STATELESS));

		return httpSecurity.build();
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
