package com.authplayground.api.presentation;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static com.authplayground.support.TestConstant.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.authplayground.api.dto.auth.request.LoginRequest;
import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.api.dto.member.request.UpdateRequest;
import com.authplayground.api.dto.token.response.TokenResponse;
import com.authplayground.support.fixture.MemberFixture;
import com.authplayground.support.helper.AuthTestHelper;
import com.authplayground.support.helper.MemberTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("MemberController 통합 테스트")
class MemberControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("[✅ SUCCESS] signUpMember - 사용자가 성공적으로 회원가입을 완료했습니다.")
	void signUpMember_returnsVoid_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

		// WHEN & THEN
		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest)
			.andExpect(status().isOk())
			.andExpect(content().string("[✅ SUCCESS] 사용자 정보를 성공적으로 생성했습니다."));
	}

	@Test
	@DisplayName("[❎ FAILURE] signUpMember - 이메일 형식이 잘못되어 회원가입에 실패합니다.")
	void signUpMember_throwsBadRequestException_whenInvalidEmail_failure() throws Exception {
		// GIVEN
		SignUpRequest wrongSignUpRequest = MemberFixture.createSignUpRequestWithWrongEmail();

		// WHEN & THEN
		MemberTestHelper.performSignUp(mockMvc, objectMapper, wrongSignUpRequest)
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("[❎ FAILURE] signUpMember - 비밀번호 확인이 일치하지 않아 회원가입에 실패합니다.")
	void signUpMember_throwsBadRequestException_whenPasswordMismatch_failure() throws Exception {
		// GIVEN
		SignUpRequest wrongSignUpRequest = MemberFixture.createSignUpRequestWithWrongPasswordCheck();

		// WHEN & THEN
		MemberTestHelper.performSignUp(mockMvc, objectMapper, wrongSignUpRequest)
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("[✅ SUCCESS] findMemberInfo - 로그인 사용자의 정보를 성공적으로 조회했습니다.")
	void findMemberInfo_returnsMemberInfoResponse_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		LoginRequest loginRequest = MemberFixture.createLoginRequest();

		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest);
		MvcResult loginResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest)
			.andReturn();

		String content = loginResult.getResponse().getContentAsString();
		TokenResponse tokenResponse = objectMapper.readValue(content, TokenResponse.class);

		// WHEN & THEN
		MemberTestHelper.performFind(mockMvc, tokenResponse)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(signUpRequest.email()))
			.andExpect(jsonPath("$.nickname").value(signUpRequest.nickname()))
			.andExpect(jsonPath("$.address").value(signUpRequest.address()));
	}

	@Test
	@DisplayName("[❎ FAILURE] findMemberInfo - 인증 없이 요청하여 사용자의 정보 조회에 실패했습니다.")
	void findMemberInfo_throwsUnauthorizedException_whenTokenMissing_failure() throws Exception {
		// GIVEN

		// WHEN & THEN
		MemberTestHelper.performFind(mockMvc)
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("[✅ SUCCESS] updateMember - 사용자의 회원 정보를 성공적으로 수정했습니다.")
	void updateMember_returnsVoid_success() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();
		LoginRequest loginRequest = MemberFixture.createLoginRequest();
		UpdateRequest updateRequest = MemberFixture.createUpdateRequest();

		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest);

		MvcResult loginResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest)
			.andReturn();

		TokenResponse tokenResponse = objectMapper
			.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);

		// WHEN & THEN
		MemberTestHelper.performUpdate(mockMvc, objectMapper, tokenResponse, updateRequest)
			.andExpect(status().isOk())
			.andExpect(content().string("[✅ SUCCESS] 사용자 정보 수정을 성공적으로 완료했습니다."));
	}

	@Test
	@DisplayName("[❎ FAILURE] updateMember - 인증 없이 요청하여 사용자의 정보 수정에 실패했습니다.")
	void updateMember_throwsUnauthorizedException_whenNoToken_failure() throws Exception {
		// GIVEN
		UpdateRequest updateRequest = MemberFixture.createUpdateRequest();

		// WHEN & THEN
		MemberTestHelper.performUpdate(mockMvc, objectMapper, updateRequest)
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("[❎ FAILURE] updateMember - 중복된 닉네임으로 요청하여 사용자의 정보 수정에 실패했습니다.")
	void updateMember_throwsConflictException_whenNicknameDuplicated_failure() throws Exception {
		// GIVEN
		SignUpRequest signUpRequest = MemberFixture.createSignUpRequest(
			"first@email.com", PASSWORD, PASSWORD, "nickname", "000000-0000000", "서울시 강남구"
		);
		SignUpRequest duplicatedSignUpRequest = MemberFixture.createSignUpRequest(
			"second@email.com", PASSWORD, PASSWORD, "otherNickname", "111111-1111111", "서울시 강남구"
		);

		MemberTestHelper.performSignUp(mockMvc, objectMapper, signUpRequest);
		MemberTestHelper.performSignUp(mockMvc, objectMapper, duplicatedSignUpRequest);

		LoginRequest loginRequest = MemberFixture.createLoginRequest("second@email.com", PASSWORD);
		MvcResult loginResult = AuthTestHelper.performLogin(mockMvc, objectMapper, loginRequest).andReturn();
		TokenResponse tokenResponse = objectMapper
			.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);

		UpdateRequest duplicatedUpdateRequest = MemberFixture.createUpdateRequestWithDuplicatedNickname();

		// WHEN & THEN
		MemberTestHelper.performUpdate(mockMvc, objectMapper, tokenResponse, duplicatedUpdateRequest)
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value(DUPLICATED_NICKNAME_FAILURE.getMessage()));
	}
}
