package com.hcs.idempotencyapi.aop;

import java.util.Optional;

public interface IdempotencyApiStrategy {

    void isValidKey(String key, String regex);
    Optional<Object> getResponseDataIfDuplicateRequest(String storeType, String key, String requestBody);
    void preProceed(String storeType, String key, String requestBody);
    void postProceed(String storeType, String key, Object result);
    void exProceed(String storeType, String key);
}
