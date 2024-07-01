package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.aop.vo.IdempotencyRequest;

public interface IdempotencyRequestExtractor {

    IdempotencyRequest prepare(IdempotencyApi idempotencyApi);

}
