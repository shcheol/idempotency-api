package com.hcs.idempotencyapi;

import com.hcs.idempotencyapi.aop.IdempotencyApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

	@IdempotencyApi
	@GetMapping
	public ResponseEntity<TestClass> get(){
		return ResponseEntity.ok(new TestClass("123"));
	}

	@IdempotencyApi(keyRequired = true)
	@PostMapping
	public ResponseEntity<TestClass> post(@RequestBody TestClass testClass) throws InterruptedException {

		log.info("call controller");
		Thread.sleep(100);
		return ResponseEntity.ok(testClass);
	}
}
