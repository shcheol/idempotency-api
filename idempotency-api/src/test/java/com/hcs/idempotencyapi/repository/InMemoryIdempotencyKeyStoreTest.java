package com.hcs.idempotencyapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class InMemoryIdempotencyKeyStoreTest {

	@Autowired
	InMemoryIdempotencyKeyStore keyStore;

	@Test
	void has() {
		String key = "1";
		String prefix = "";

		boolean has = keyStore.has(prefix,key);
		assertThat(has).isFalse();
		keyStore.set(prefix,key, "");
		boolean hasAfterSet = keyStore.has(prefix, key);
		assertThat(hasAfterSet).isTrue();
	}

	@Test
	void set() {
		String key = "2";
		String prefix = "";

		Object o = keyStore.get(prefix,key);
		keyStore.set(prefix,key, "");

	}

	@Test
	void get() {
	}
}