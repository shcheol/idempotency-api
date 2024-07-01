package com.hcs.idempotencyapi.aop.vo;

import com.hcs.idempotencyapi.ex.IdempotentError;
import com.hcs.idempotencyapi.ex.IdempotentException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public record IdempotencyKey(String key) {

	public static IdempotencyKey create(String key) {
		return new IdempotencyKey(key);
	}

	public void isValid(String regex, boolean isRequired) {

		if (isEmpty() && isRequired) {
			throw new IdempotentException(IdempotentError.NO_KEY);
		}

		if (StringUtils.hasText(regex) && !Pattern.compile(regex).matcher(key).matches()) {
			throw new IdempotentException(IdempotentError.INVALID_KEY);
		}
	}

	public boolean isEmpty() {
		return !StringUtils.hasText(this.key);
	}
}
