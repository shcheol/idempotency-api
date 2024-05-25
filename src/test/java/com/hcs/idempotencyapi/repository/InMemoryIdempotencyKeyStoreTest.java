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

        boolean has = keyStore.has(key);
        assertThat(has).isFalse();
        keyStore.set(key, "");
        boolean hasAfterSet = keyStore.has(key);
        assertThat(hasAfterSet).isTrue();
    }

    @Test
    void getSet() {
        String key = "2";
        String prefix = "";
        String value = "val";
        Object get = keyStore.get(key);
        assertThat(get).isNull();

        keyStore.set(key, value);
        Object getAfterSet = keyStore.get(key);
        assertThat(getAfterSet).isEqualTo(value);
    }

}