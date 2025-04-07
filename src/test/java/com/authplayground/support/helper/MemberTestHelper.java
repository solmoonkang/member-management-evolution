package com.authplayground.support.helper;

import static com.authplayground.support.TestConstant.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.authplayground.api.dto.member.request.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MemberTestHelper {

	public static ResultActions performSignup(MockMvc mockMvc, ObjectMapper objectMapper, SignUpRequest request) throws Exception {
		return mockMvc.perform(post(SIGNUP_URL)
			.contentType(JSON)
			.content(objectMapper.writeValueAsString(request)));
	}
}
