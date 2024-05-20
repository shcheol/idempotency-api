package com.hcs.idempotencyapi.repository.mapper;

import com.hcs.idempotencyapi.repository.IdempotencyKeyStoreFactory;
import org.springframework.stereotype.Component;

@Component
public class ResponseStoreMapper {

    private final IdempotencyKeyStoreFactory idempotencyKeyStoreFactory;

    private final String RESPONSE_PREFIX = "RESPONSE_";

    public ResponseStoreMapper(IdempotencyKeyStoreFactory idempotencyKeyStoreFactory) {
        this.idempotencyKeyStoreFactory = idempotencyKeyStoreFactory;
    }
    public boolean has(String storeType, String key) {
        return idempotencyKeyStoreFactory.get(storeType).has(RESPONSE_PREFIX + key);
    }

    public void set(String storeType, String key, Object value) {
        idempotencyKeyStoreFactory.get(storeType).set(RESPONSE_PREFIX + key, value);
    }

    public Object get(String storeType, String key) {
		return idempotencyKeyStoreFactory.get(storeType).get(RESPONSE_PREFIX + key);
    }

    public void remove(String storeType, String key) {
        idempotencyKeyStoreFactory.get(storeType).remove(RESPONSE_PREFIX + key);
    }
}
