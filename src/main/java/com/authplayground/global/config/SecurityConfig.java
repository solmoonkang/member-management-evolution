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

import com.authplayground.global.auth.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return webSecurityCustomizer -> webSecurityCustomizer.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.requestMatchers("/h2-console/**");
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable);

		httpSecurity.authorizeHttpRequests(auth -> auth
			.requestMatchers("/", "/api/signup", "/api/login").permitAll()
			.anyRequest().authenticated());

		httpSecurity.logout(logout -> logout
			.logoutSuccessUrl("/api/login")
			.invalidateHttpSession(true));

		httpSecurity.userDetailsService(customUserDetailsService);

		httpSecurity.sessionManagement(session -> session
			.sessionCreationPolicy(STATELESS));

		return httpSecurity.build();
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
