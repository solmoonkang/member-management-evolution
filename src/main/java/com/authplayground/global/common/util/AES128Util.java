package com.authplayground.global.common.util;

import static com.authplayground.global.error.model.ErrorMessage.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.authplayground.global.error.exception.AuthPlaygroundException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AES128Util {

	private static final Charset UTF_8 = StandardCharsets.UTF_8;
	private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	private static final int IV_SIZE = 16;

	@Value("${aes.secret-key}")
	private String secretKey;

	private SecretKeySpec secretKeySpec;

	@PostConstruct
	public void init() {
		try {
			secretKeySpec = new SecretKeySpec(secretKey.getBytes(UTF_8), ALGORITHM);
		} catch (Exception e) {
			log.error("[✅ LOGGER] AES 초기화 중 예외가 발생했습니다.", e);
			throw new AuthPlaygroundException(FAILED_AES_INIT_FAILURE.getMessage());
		}
	}

	public String encryptText(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			byte[] iv = generateInitVector();
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));

			byte[] encryptedWithIv = new byte[IV_SIZE + encrypted.length];
			System.arraycopy(iv, 0, encryptedWithIv, 0, IV_SIZE);
			System.arraycopy(encrypted, 0, encryptedWithIv, IV_SIZE, encrypted.length);

			return Base64.getEncoder().encodeToString(encryptedWithIv);
		} catch (Exception e) {
			log.error("[✅ LOGGER] AES 암호화 중 오류가 발생했습니다.", e);
			throw new AuthPlaygroundException(FAILED_AES_ENCRYPT_FAILURE.getMessage());
		}
	}

	public String decryptText(String encryptedText) {
		try {
			byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText.getBytes(UTF_8));
			byte[] iv = Arrays.copyOfRange(encryptedWithIv, 0, IV_SIZE);
			byte[] encrypted = Arrays.copyOfRange(encryptedWithIv, IV_SIZE, encryptedWithIv.length);

			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] decrypted = cipher.doFinal(encrypted);

			return new String(decrypted, UTF_8);
		} catch (Exception e) {
			log.error("[✅ LOGGER] AES 복호화 중 오류가 발생했습니다.", e);
			throw new AuthPlaygroundException(FAILED_AES_DECRYPT_FAILURE.getMessage());
		}
	}

	private byte[] generateInitVector() {
		byte[] iv = new byte[IV_SIZE];
		new SecureRandom().nextBytes(iv);
		return iv;
	}
}
