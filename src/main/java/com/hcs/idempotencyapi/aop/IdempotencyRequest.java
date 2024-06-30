package com.hcs.idempotencyapi.aop;

public record IdempotencyRequest(IdempotencyKey key, String body) {
}
