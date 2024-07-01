package com.hcs.idempotencyapi.aop.code;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TestService {

	public String test(String value){
		return value;
	}

	public String ex(String value){
		throw new RuntimeException();
	}

	public String delay(String value){
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return "response" + value;
	}
}
