package com.hcs.idempotencyapi.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryIdempotencyKeyStore implements IdempotencyKeyStore {

	private final Map<String, Object> keyStore = new ConcurrentHashMap<>();

	@Override
	public boolean has(String key) {
		return keyStore.containsKey(key);
	}

	@Override
	public void set(String key, Object value) {
		keyStore.putIfAbsent(key, value);
	}

	@Override
	public Object get(String key) {
		return keyStore.get(key);
	}

	@Override
	public void remove(String key) {
		keyStore.remove(key);
	}
}
