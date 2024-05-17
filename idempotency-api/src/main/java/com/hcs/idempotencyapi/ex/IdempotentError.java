package com.hcs.idempotencyapi.ex;

import org.springframework.http.HttpStatus;

public enum IdempotentError {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "멱등키가 누락되었습니다."),
	CONFLICT(HttpStatus.CONFLICT, "이전 요청 처리가 진행중입니다."),
	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "요청 본문이 처음 요청과 다릅니다.")
	;

	private final HttpStatus status;

	private final String message;

	public HttpStatus status() {
		return status;
	}

	public String message() {
		return message;
	}

	IdempotentError(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
