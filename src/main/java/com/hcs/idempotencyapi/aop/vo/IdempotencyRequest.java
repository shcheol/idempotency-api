package com.hcs.idempotencyapi.aop.vo;

import com.hcs.idempotencyapi.aop.IdempotencyApi;

public record IdempotencyRequest(IdempotencyKey key, String body, IdempotencyApi idempotencyApi) {

	public static IdempotencyRequest create(String key, String body, IdempotencyApi idempotencyApi) {
		return new IdempotencyRequest(IdempotencyKey.create(key), body, idempotencyApi);
	}
}
