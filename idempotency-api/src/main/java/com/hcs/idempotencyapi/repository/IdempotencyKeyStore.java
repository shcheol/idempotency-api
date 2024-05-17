package com.hcs.idempotencyapi.repository;

public interface IdempotencyKeyStore {

	boolean has(String prefix, String key);
	void set(String prefix, String key, Object value);
	Object get(String prefix, String key);
}
