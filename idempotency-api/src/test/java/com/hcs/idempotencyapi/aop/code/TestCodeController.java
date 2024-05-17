package com.hcs.idempotencyapi.aop.code;

import com.hcs.idempotencyapi.aop.IdempotencyApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestCodeController {

	@IdempotencyApi(keyRequired = true)
	@PostMapping("/required")
	public ResponseEntity<TestCodeClass> keyRequired(@RequestBody TestCodeClass testClass) {

		return ResponseEntity.ok(testClass);
	}

	@IdempotencyApi(keyRequired = true)
	@PostMapping("/required/delay")
	public ResponseEntity<TestCodeClass> keyRequiredDelay(@RequestBody TestCodeClass testClass) {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return ResponseEntity.ok(testClass);
	}

	@IdempotencyApi(keyRequired = false)
	@PostMapping("/no-required")
	public ResponseEntity<TestCodeClass> noKeyRequired(@RequestBody TestCodeClass testClass) {

		return ResponseEntity.ok(testClass);
	}
}
