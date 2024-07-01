package com.hcs.idempotencyapi.aop.vo;

import com.hcs.idempotencyapi.ex.IdempotentException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IdempotencyKeyTest {
	@Test
	void keyPatternMatch() {
		String key = UUID.randomUUID().toString();
		String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

		IdempotencyKey idempotencyKey = IdempotencyKey.create(key);
		idempotencyKey.isValid(regex, true);
		idempotencyKey.isValid("", true);
	}

	@Test
	void keyPatternNotMatch() {
		String key = "invalidKey";
		String regex = "/^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$/";
		IdempotencyKey i = IdempotencyKey.create(key);

		assertThrows(IdempotentException.class, () -> i.isValid(regex, true));
	}

	@Test
	void keyEmpty() {
		String key = "";
		String regex = "regex";
		IdempotencyKey i = IdempotencyKey.create(key);

		assertThrows(IdempotentException.class, () ->  i.isValid(regex, true));
	}

}