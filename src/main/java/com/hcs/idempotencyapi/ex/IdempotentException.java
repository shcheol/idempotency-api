package com.hcs.idempotencyapi.ex;

public class IdempotentException extends RuntimeException {

	private final IdempotentError error;

	public IdempotentError getError() {
		return error;
	}

	public IdempotentException(IdempotentError error) {
		super(error.message());
		this.error = error;
	}
}
