package com.hcs.idempotencyapi.aop.code;

import com.hcs.idempotencyapi.aop.IdempotencyApi;
import com.hcs.idempotencyapi.ex.IdempotentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestCodeController {

	private final TestService testService;

	public TestCodeController(TestService testService) {
		this.testService = testService;
	}

	@IdempotencyApi(keyRequired = true)
	@PostMapping("/required")
	public ResponseEntity<TestCodeClass> keyRequired(@RequestBody TestCodeClass testClass) {
		String test = testService.test(testClass.value());
		return ResponseEntity.ok(new TestCodeClass(test));
	}

	@IdempotencyApi(keyRequired = true)
	@PostMapping("/required/delay")
	public ResponseEntity<TestCodeClass> keyRequiredDelay(@RequestBody TestCodeClass testClass) {
		String test = testService.delay(testClass.value());
		return ResponseEntity.ok(new TestCodeClass(test));
	}

	@IdempotencyApi(keyRequired = true)
	@PostMapping("/required/ex")
	public ResponseEntity<TestCodeClass> keyRequiredThrow(@RequestBody TestCodeClass testClass) {
		String ex = testService.ex(testClass.value());
		return ResponseEntity.ok(new TestCodeClass(ex));
	}

	@IdempotencyApi(keyRequired = false)
	@PostMapping("/no-required")
	public ResponseEntity<TestCodeClass> noKeyRequired(@RequestBody TestCodeClass testClass) {
		String test = testService.test(testClass.value());
		return ResponseEntity.ok(new TestCodeClass(test));
	}

	@ExceptionHandler(value = IdempotentException.class)
	public ResponseEntity<String> errorHandler(IdempotentException idempotentException){
		return ResponseEntity.status(idempotentException.getError().status()).body(idempotentException.getMessage());
	}
}
