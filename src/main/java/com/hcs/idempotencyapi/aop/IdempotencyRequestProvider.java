package com.hcs.idempotencyapi.aop;

public interface IdempotencyRequestProvider {

    IdempotencyRequest prepare(IdempotencyApi idempotencyApi);

}
