package com.hcs.idempotencyapi.repository;

public interface IdempotencyKeyStore {

	boolean has(String key);
	void set(String key, Object value);
	Object get(String key);
	void remove(String key);
}
