package com.hcs.idempotencyapi.aop;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiCallCases {

	static ExtractableResponse<Response> 헤더에멱등키없는요청(String path) {

		return RestAssured
				.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body("""
							{
							"value":"testValue"
							}
						""")
				.when()
				.post("/"+path)
				.then()
				.log().all().extract();
	}

	static ExtractableResponse<Response> 멱등키요청(String path, String key) {

		return RestAssured
				.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("idempotent",key)
				.body("""
							{
							"value":"testValue"
							}
						""")
				.when()
				.post("/"+path)
				.then()
				.log().all().extract();
	}

	static ExtractableResponse<Response> 멱등키요청_동일키_다른본문 (String path, String key) {

		return RestAssured
				.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header("idempotent",key)
				.body("""
							{
							"value":"otherValue"
							}
						""")
				.when()
				.post("/"+path)
				.then()
				.log().all().extract();
	}
}
