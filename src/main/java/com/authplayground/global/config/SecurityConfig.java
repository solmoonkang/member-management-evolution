package com.authplayground.global.config;

import static com.authplayground.global.common.util.AuthConstant.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.authplayground.api.domain.member.model.Role;
import com.authplayground.global.auth.filter.JwtAuthenticationFilter;
import com.authplayground.global.auth.handler.CustomAccessDeniedHandler;
import com.authplayground.global.auth.handler.CustomAuthenticationEntryPoint;
import com.authplayground.global.auth.token.JwtProvider;
import com.authplayground.global.auth.validator.TokenValidator;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtProvider jwtProvider;
	private final TokenValidator tokenValidator;
	private final HandlerExceptionResolver handlerExceptionResolver;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return webSecurity -> webSecurity.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.requestMatchers(SECURITY_IGNORED_URLS);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(IF_REQUIRED)
			.sessionFixation().migrateSession());

		httpSecurity.authorizeHttpRequests(auth -> auth
			.requestMatchers(PUBLIC_API_PATHS).permitAll()
			.requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
			.anyRequest().authenticated());

		httpSecurity.addFilterBefore(
			new JwtAuthenticationFilter(jwtProvider, tokenValidator, handlerExceptionResolver),
			UsernamePasswordAuthenticationFilter.class);

		httpSecurity.exceptionHandling(exceptionHandling -> exceptionHandling
			.accessDeniedHandler(customAccessDeniedHandler)
			.authenticationEntryPoint(customAuthenticationEntryPoint));

		return httpSecurity.build();
	}
}
