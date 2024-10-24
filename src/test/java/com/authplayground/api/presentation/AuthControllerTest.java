package com.authplayground.api.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.authplayground.api.application.AuthenticationService;
import com.authplayground.api.application.MemberService;
import com.authplayground.api.dto.member.LoginRequest;
import com.authplayground.api.dto.member.SignUpRequest;
import com.authplayground.support.MemberFixture;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	MemberService memberService;

	@MockBean
	AuthenticationService authenticationService;

	@Test
	@WithMockUser
	@DisplayName("SIGNUP MEMBER (⭕️ SUCCESS): 사용자가 성공적으로 회원가입을 완료했습니다.")
	void signUpMember_void_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.signUpMemberRequest();

		// WHEN & THEN
		mockMvc.perform(post("/api/signup")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(signUpRequest)))
			.andExpect(status().isCreated())
			.andExpect(content().string("[✅ SUCCESS] 회원가입이 성공적으로 완료되었습니다."));

		verify(memberService).signUpMember(signUpRequest);
	}

	@Test
	@WithMockUser
	@DisplayName("LOGIN MEMBER (⭕️ SUCCESS): 사용자가 성공적으로 로그인을 완료했습니다.")
	void loginMember_void_success() throws Exception {
		// GIVEN
		LoginRequest loginRequest = MemberFixture.loginMemberRequest();

		// WHEN & THEN
		mockMvc.perform(post("/api/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("[✅ SUCCESS] 로그인이 성공적으로 완료되었습니다."));
	}
}
