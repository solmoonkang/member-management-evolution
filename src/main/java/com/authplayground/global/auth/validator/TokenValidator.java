package com.authplayground.global.auth.validator;

import static com.authplayground.global.error.model.ErrorMessage.*;

import org.springframework.stereotype.Component;

import com.authplayground.api.domain.member.repository.BlacklistRepository;
import com.authplayground.global.error.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenValidator {

	private final BlacklistRepository blacklistRepository;

	public void validateTokenNotBlacklisted(String token) {
		if (blacklistRepository.isBlacklisted(token)) {
			throw new UnauthorizedException(BLACKLISTED_ACCESS_TOKEN);
		}
	}
}
