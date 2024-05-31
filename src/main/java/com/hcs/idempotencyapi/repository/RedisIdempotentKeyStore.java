package com.hcs.idempotencyapi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
		redisStore.setIfAbsent(key, writeValueAsString(value), 300, TimeUnit.SECONDS);
	}

	@Override
	public Object get(String key) {
		return readValue(redisStore.get(key));
	}

	@Override
	public Object remove(String key) {
		return readValue(redisStore.getAndDelete(key));
	}

	private Object readValue(String value) {
		try {
			return om.readValue(value, new TypeReference<Object>() {
			});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String writeValueAsString(Object value) {
		try {
			return om.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}


}
