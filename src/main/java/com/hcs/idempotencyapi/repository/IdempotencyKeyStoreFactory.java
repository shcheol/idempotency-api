package com.hcs.idempotencyapi.repository;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class IdempotencyKeyStoreFactory {

    private final Map<String, IdempotencyKeyStore> idempotencyKeyStoreMap;

    public IdempotencyKeyStoreFactory(Map<String, IdempotencyKeyStore> idempotencyKeyStoreMap) {
        if (idempotencyKeyStoreMap== null || idempotencyKeyStoreMap.isEmpty()){
            throw new NoIdempotencyKeyStoreException();
        }
        this.idempotencyKeyStoreMap = idempotencyKeyStoreMap;
    }

    public IdempotencyKeyStore get(String storeBeanName){
        return Optional.ofNullable(idempotencyKeyStoreMap.get(storeBeanName))
                .orElseThrow(NoIdempotencyKeyStoreException::new);
    }
}
