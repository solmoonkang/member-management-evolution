package com.authplayground.support.helper;

import static com.authplayground.global.common.util.JwtConstant.*;
import static com.authplayground.support.TestConstant.*;
import static com.authplayground.support.TestConstant.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthTestHelper {

	public static ResultActions performLogin(MockMvc mockMvc, ObjectMapper objectMapper, LoginRequest request) throws Exception {
		return mockMvc.perform(post(LOGIN_URL)
			.contentType(JSON)
			.content(objectMapper.writeValueAsString(request)));
	}

	public static ResultActions performLogout(MockMvc mockMvc, String accessToken) throws Exception {
		return mockMvc.perform(post(LOGOUT_URL)
			.header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + accessToken));
	}

	public static ResultActions performLogout(MockMvc mockMvc) throws Exception {
		return mockMvc.perform(post(LOGOUT_URL));
	}

	public static ResultActions performReissue(MockMvc mockMvc, TokenResponse tokenResponse) throws Exception {
		return mockMvc.perform(post(REISSUE_URL)
			.header(AUTHORIZATION_HEADER, BEARER_TYPE + tokenResponse.accessToken())
			.header(REFRESH_TOKEN_HEADER, tokenResponse.refreshToken()));
	}

	public static ResultActions performReissue(MockMvc mockMvc, String accessToken) throws Exception {
		return mockMvc.perform(post(REISSUE_URL)
			.header(HttpHeaders.AUTHORIZATION, accessToken));
	}

	public static ResultActions performReissue(MockMvc mockMvc, String accessToken, String refreshToken) throws Exception {
		return mockMvc.perform(post(REISSUE_URL)
			.header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + accessToken)
			.header(REFRESH_TOKEN_HEADER, refreshToken));
	}
}
