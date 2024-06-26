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
	private final ObjectMapper om;

	public RedisIdempotentKeyStore(RedisTemplate<String, String> redisTemplate, ObjectMapper om) {
		this.redisStore = redisTemplate.opsForValue();
		this.om = om;
	}

	@Override
	public boolean has(String key) {
		return StringUtils.hasText(redisStore.get(key));
	}

	@Override
	public void set(String key, Object value) {

		try {
			String s = om.writeValueAsString(value);
			redisStore.setIfAbsent(key, s, 300, TimeUnit.SECONDS);
		} catch (JsonProcessingException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public Object get(String key) {
		return redisStore.getAndDelete(key);
	}

	@Override
	public Object remove(String key) {
		return redisStore.getAndDelete(key);
	}


}
