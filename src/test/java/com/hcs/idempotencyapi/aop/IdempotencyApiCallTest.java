package com.hcs.idempotencyapi.aop;

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
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.concurrent.CompletableFuture;

import static com.hcs.idempotencyapi.aop.ApiCallCases.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class IdempotencyApiCallTest {

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
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
	}

	@Test
	@DisplayName("멱등성 요청")
	void idempotentRequest() {

		ExtractableResponse<Response> extract = 멱등키요청("required");
		ExtractableResponse<Response> extract2 = 멱등키요청("required");

		assertThat(extract.response().statusCode()).isEqualTo(extract2.response().statusCode());
		assertThat(extract.response().body().asString()).isEqualTo(extract2.response().body().asString());
	}

	@Test
	@DisplayName("다른 payload")
	void sameKeyDiffPayload() {

		ExtractableResponse<Response> extract = 멱등키요청("required");
		ExtractableResponse<Response> extract2 = 멱등키요청_동일키_다른본문("required");
		int i = extract2.response().statusCode();

		assertThat(i).isEqualTo(IdempotentError.UNPROCESSABLE_ENTITY.status().value());
		assertThat(extract2.body().asString()).isEqualTo(IdempotentError.UNPROCESSABLE_ENTITY.message());
	}

	@Test
	@DisplayName("진행중에 추가 요청")
	void idempotencyRequest() throws InterruptedException {

		CompletableFuture.supplyAsync(() -> 멱등키요청("required/delay"));

		Thread.sleep(3000);

		ExtractableResponse<Response> extract2 = 멱등키요청("required/delay");
		int i = extract2.response().statusCode();
		assertThat(i).isEqualTo(IdempotentError.CONFLICT.status().value());
		assertThat(extract2.body().asString()).isEqualTo(IdempotentError.CONFLICT.message());
	}

}

