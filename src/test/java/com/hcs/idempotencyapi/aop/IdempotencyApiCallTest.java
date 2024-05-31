package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.aop.code.TestService;
import com.hcs.idempotencyapi.ex.IdempotentError;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.hcs.idempotencyapi.aop.ApiCallCases.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class IdempotencyApiCallTest {

	@LocalServerPort
	private int port;

	@MockBean
	TestService testService;

	@BeforeEach
	void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
		when(testService.test("testValue")).thenReturn("testValue");
		when(testService.delay("testValue")).thenCallRealMethod();
	}

	@Test
	@DisplayName("헤더에 멱등키 없는 요청, keyRequired=false")
	void noKeyAndNoRequired() {

		ExtractableResponse<Response> extract = 헤더에멱등키없는요청("no-required");
		int i = extract.response().statusCode();
		assertThat(i).isEqualTo(200);
	}

	@Test
	@DisplayName("헤더에 멱등키 없는 요청, keyRequired=true")
	void noKeyAndRequired() {

		ExtractableResponse<Response> extract = 헤더에멱등키없는요청("required");
		int i = extract.response().statusCode();
		assertThat(i).isEqualTo(IdempotentError.BAD_REQUEST.status().value());
		assertThat(extract.body().asString()).isEqualTo(IdempotentError.BAD_REQUEST.message());
		verify(testService, times(0)).test(any());

	}

	@Test
	@DisplayName("멱등성 요청")
	void idempotentRequest() {
		String key = UUID.randomUUID().toString();
		ExtractableResponse<Response> extract = 멱등키요청("required", key);
		ExtractableResponse<Response> extract2 = 멱등키요청("required", key);

		assertThat(extract.response().statusCode()).isEqualTo(extract2.response().statusCode());
		assertThat(extract.response().body().asString()).isEqualTo(extract2.response().body().asString());
		verify(testService, times(1)).test("testValue");
	}

	@Test
	@DisplayName("다른 payload")
	void sameKeyDiffPayload() {
		String key = UUID.randomUUID().toString();
		ExtractableResponse<Response> extract = 멱등키요청("required", key);
		ExtractableResponse<Response> extract2 = 멱등키요청_동일키_다른본문("required", key);
		int i = extract2.response().statusCode();

		assertThat(i).isEqualTo(IdempotentError.UNPROCESSABLE_ENTITY.status().value());
		assertThat(extract2.body().asString()).isEqualTo(IdempotentError.UNPROCESSABLE_ENTITY.message());
		verify(testService, times(1)).test("testValue");
		verify(testService, times(0)).test("otherValue");
	}

	@Test
	@DisplayName("진행중에 추가 요청")
	void idempotencyRequest() {
		String key = UUID.randomUUID().toString();
		List<CompletableFuture<ExtractableResponse<Response>>> list = new ArrayList<>();

		int requestTimes=10;
		for(int i=0; i< requestTimes;i++){
			list.add(CompletableFuture.supplyAsync(() -> 멱등키요청("required/delay", key)));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		List<ExtractableResponse<Response>> collect = list.stream()
				.map(req -> {
					try {
						return req.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}).toList();


		Optional<ExtractableResponse<Response>> any = collect.stream().filter(response ->
				response.response().statusCode() == IdempotentError.CONFLICT.status().value()).findAny();
		assertThat(any).isPresent();
		verify(testService, atMost(requestTimes)).delay("testValue");
	}

}

