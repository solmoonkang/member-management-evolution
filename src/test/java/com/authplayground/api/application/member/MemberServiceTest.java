package com.authplayground.api.application.member;

import static com.authplayground.global.error.model.ErrorMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.authplayground.api.dto.member.request.SignUpRequest;
import com.authplayground.global.common.util.AES128Util;
import com.authplayground.global.error.exception.BadRequestException;
import com.authplayground.global.error.exception.ConflictException;
import com.authplayground.support.MemberFixture;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	MemberService memberService;

	@Mock
	AES128Util aes128Util;

	@Mock
	MemberReadService memberReadService;

	@Mock
	MemberWriteService memberWriteService;

	@Nested
	@DisplayName("signUpMember() 테스트: ")
	class SignUpMember {

		@Test
		@DisplayName("[✅ SUCCESS] signUpMember - 사용자가 성공적으로 회원가입을 완료했습니다.")
		void signUpMember_returnsVoid_success() {
			// GIVEN
			String encodedRegistrationNumber = "encryptedRegistrationNumber";
			SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

			when(aes128Util.encryptText(signUpRequest.registrationNumber())).thenReturn(encodedRegistrationNumber);

			// WHEN
			memberService.signUpMember(signUpRequest);

			// THEN
			verify(memberReadService).validateMemeberEmailDuplication(signUpRequest.email());
			verify(memberReadService).validateMemberNicknameDuplication(signUpRequest.nickname());
			verify(memberReadService).validateMemberRegistrationNumberDuplication(encodedRegistrationNumber);
			verify(memberReadService).validatePasswordConfirmationMatch(signUpRequest.password(),
				signUpRequest.passwordCheck());

			verify(memberWriteService).createMember(signUpRequest, encodedRegistrationNumber);
		}

		@Test
		@DisplayName("[❎ FAILURE] signUpMember - 이미 존재하는 이메일로 요청하여 회원가입에 실패했습니다.")
		void signUpMember_throwsConflictException_whenEmailDuplicated_failure() {
			// GIVEN
			String encodedRegistrationNumber = "encryptedRegistrationNumber";
			SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

			when(aes128Util.encryptText(signUpRequest.registrationNumber())).thenReturn(encodedRegistrationNumber);

			doThrow(new ConflictException(DUPLICATED_EMAIL_FAILURE))
				.when(memberReadService).validateMemeberEmailDuplication(signUpRequest.email());

			// WHEN & THEN
			assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining(DUPLICATED_EMAIL_FAILURE.getMessage());
		}

		@Test
		@DisplayName("[❎ FAILURE] signUpMember - 이미 존재하는 닉네임으로 요청하여 회원가입에 실패했습니다.")
		void signUpMember_throwsConflictException_whenNicknameDuplicated_failure() {
			// GIVEN
			String encodedRegistrationNumber = "encryptedRegistrationNumber";
			SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

			when(aes128Util.encryptText(signUpRequest.registrationNumber())).thenReturn(encodedRegistrationNumber);

			doThrow(new ConflictException(DUPLICATED_NICKNAME_FAILURE))
				.when(memberReadService).validateMemberNicknameDuplication(signUpRequest.nickname());

			// WHEN & THEN
			assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining(DUPLICATED_NICKNAME_FAILURE.getMessage());
		}

		@Test
		@DisplayName("[❎ FAILURE] signUpMember - 이미 존재하는 주민번호로 요청하여 회원가입에 실패했습니다.")
		void signUpMember_throwsConflictException_whenRegistrationNumberDuplicated_failure() {
			// GIVEN
			String encodedRegistrationNumber = "encryptedRegistrationNumber";
			SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

			when(aes128Util.encryptText(signUpRequest.registrationNumber())).thenReturn(encodedRegistrationNumber);

			doThrow(new ConflictException(DUPLICATED_REGISTRATION_NUMBER_FAILURE))
				.when(memberReadService).validateMemberRegistrationNumberDuplication(encodedRegistrationNumber);

			// WHEN & THEN
			assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining(DUPLICATED_REGISTRATION_NUMBER_FAILURE.getMessage());
		}

		@Test
		@DisplayName("[❎ FAILURE] signUpMember - 입력하신 비밀번호와 확인 비밀번호가 틀려서 회원가입에 실패했습니다.")
		void signUpMember_throwsBadRequestException_whenPasswordConfirmationMatch_failure() {
			// GIVEN
			String encodedRegistrationNumber = "encryptedRegistrationNumber";
			SignUpRequest signUpRequest = MemberFixture.createSignUpRequest();

			when(aes128Util.encryptText(signUpRequest.registrationNumber())).thenReturn(encodedRegistrationNumber);

			doNothing().when(memberReadService).validateMemeberEmailDuplication(signUpRequest.email());
			doNothing().when(memberReadService).validateMemberNicknameDuplication(signUpRequest.nickname());
			doNothing().when(memberReadService).validateMemberRegistrationNumberDuplication(encodedRegistrationNumber);

			doThrow(new BadRequestException(PASSWORD_MISMATCH_FAILURE))
				.when(memberReadService)
				.validatePasswordConfirmationMatch(signUpRequest.password(), signUpRequest.passwordCheck());

			// WHEN & THEN
			assertThatThrownBy(() -> memberService.signUpMember(signUpRequest))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining(PASSWORD_MISMATCH_FAILURE.getMessage());
		}
	}
}
