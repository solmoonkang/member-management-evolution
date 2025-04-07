package com.authplayground.support.helper;

import static com.authplayground.support.TestConstant.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MemberTestHelper {

	public static ResultActions performSignUp(MockMvc mockMvc, ObjectMapper objectMapper, SignUpRequest request) throws Exception {
		return mockMvc.perform(post(SIGNUP_URL)
			.contentType(JSON)
			.content(objectMapper.writeValueAsString(request)));
	}

	public static ResultActions performFind(MockMvc mockMvc, TokenResponse tokenResponse) throws Exception {
		return mockMvc.perform(get(MEMBER_URL)
			.header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + tokenResponse.accessToken()));
	}

	public static ResultActions performFind(MockMvc mockMvc) throws Exception {
		return mockMvc.perform(get(MEMBER_URL));
	}

	public static ResultActions performUpdate(MockMvc mockMvc, ObjectMapper objectMapper, TokenResponse tokenResponse, UpdateRequest updateRequest) throws Exception {
		return mockMvc.perform(put(MEMBER_URL)
			.header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + tokenResponse.accessToken())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)));
	}

	public static ResultActions performUpdate(MockMvc mockMvc, ObjectMapper objectMapper, UpdateRequest updateRequest) throws Exception {
		return mockMvc.perform(put(MEMBER_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)));
	}

	public static ResultActions performDelete(MockMvc mockMvc, TokenResponse tokenResponse) throws Exception {
		return mockMvc.perform(delete(MEMBER_URL)
			.header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + tokenResponse.accessToken()));
	}

	public static ResultActions performDelete(MockMvc mockMvc) throws Exception {
		return mockMvc.perform(delete(MEMBER_URL));
	}
}
