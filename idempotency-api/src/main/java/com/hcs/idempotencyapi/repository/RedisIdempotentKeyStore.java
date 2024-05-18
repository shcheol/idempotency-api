package com.hcs.idempotencyapi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
public class RedisIdempotentKeyStore implements IdempotencyKeyStore {

    private final ValueOperations<String, String> redisStore;

    public RedisIdempotentKeyStore(RedisTemplate<String, String> redisTemplate) {
        this.redisStore = redisTemplate.opsForValue();
    }

    @Override
    public boolean has(String prefix, String key) {
        String s = redisStore.get(prefix + key);

        return StringUtils.hasText(s);
    }

    @Override
    public void set(String prefix, String key, Object value) {
        ObjectMapper om = new ObjectMapper();
        try {
            String s = om.writeValueAsString(value);
            redisStore.setIfAbsent(prefix+key, s, 300, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Object get(String prefix, String key) {
        return redisStore.getAndDelete(prefix+key);
    }
}
