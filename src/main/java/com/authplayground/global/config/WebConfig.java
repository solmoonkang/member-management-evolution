package com.authplayground.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.authplayground.global.config.security.AuthMemberArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final AuthMemberArgumentResolver authMemberArgumentResolver;

	public WebConfig(AuthMemberArgumentResolver authMemberArgumentResolver) {
		this.authMemberArgumentResolver = authMemberArgumentResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers) {
		handlerMethodArgumentResolvers.add(authMemberArgumentResolver);
	}
}
