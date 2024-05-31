package com.hcs.idempotencyapi.repository.mapper;

import com.hcs.idempotencyapi.repository.IdempotencyKeyStoreFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestStoreMapper {

	private final IdempotencyKeyStoreFactory idempotencyKeyStoreFactory;

	private final String REQUEST_PREFIX = "REQUEST_";

	public RequestStoreMapper(IdempotencyKeyStoreFactory idempotencyKeyStoreFactory) {
		this.idempotencyKeyStoreFactory = idempotencyKeyStoreFactory;
	}

	public boolean has(String storeType, String key) {
		return idempotencyKeyStoreFactory.get(storeType).has(REQUEST_PREFIX + key);
	}

	public void set(String storeType, String key, Object value) {
		idempotencyKeyStoreFactory.get(storeType).set(REQUEST_PREFIX + key, value);
	}

	public Object get(String storeType, String key) {
		return idempotencyKeyStoreFactory.get(storeType).get(REQUEST_PREFIX + key);
	}

	public Object remove(String storeType, String key) {
		return idempotencyKeyStoreFactory.get(storeType).remove(REQUEST_PREFIX + key);
	}
}
