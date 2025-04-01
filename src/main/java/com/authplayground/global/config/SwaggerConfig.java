package com.authplayground.global.config;

import static com.authplayground.global.common.util.SwaggerConstant.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(apiInfo())
			.addSecurityItem(securityRequirement())
			.components(components());
	}

	private Info apiInfo() {
		return new Info()
			.title(API_TITLE)
			.description(API_DESCRIPTION)
			.version(API_VERSION);
	}

	private SecurityRequirement securityRequirement() {
		return new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
	}

	private Components components() {
		return new Components()
			.addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme());
	}

	private SecurityScheme createSecurityScheme() {
		return new SecurityScheme()
			.name(AUTH_HEADER_NAME)
			.type(SecurityScheme.Type.HTTP)
			.scheme(BEARER_TYPE)
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.description(SECURITY_SCHEME_DESCRIPTION);
	}
}
