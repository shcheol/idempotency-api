package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.ex.IdempotentException;
import com.hcs.idempotencyapi.repository.mapper.RequestStoreMapper;
import com.hcs.idempotencyapi.repository.mapper.ResponseStoreMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdempotentTemplateTest {

	@Autowired
	IdempotentTemplate strategyLogic;

	@Autowired
	RequestStoreMapper requestStore;
	@Autowired
	ResponseStoreMapper responseStore;

	String storeType = "inMemoryIdempotencyKeyStore";

	@Test
	void getResponseDataIfDuplicateRequest() {

		String key = "getResponseDataIfDuplicateRequest";
		String body = """
				{"request":"body"}
				""";
		Optional<Object> test1 = strategyLogic.getResponseDataIfDuplicateRequest(storeType, key, body);
		assertThat(test1).isEmpty();

		strategyLogic.preProceed(storeType, key, body);
		assertThrows(IdempotentException.class, () -> strategyLogic.getResponseDataIfDuplicateRequest(storeType, key, body).get());

		Object result = "result";
		strategyLogic.postProceed(storeType, key, result);

		String differentBody = """
				{"request":"differentBody"}
				""";
		assertThrows(IdempotentException.class, () -> strategyLogic.getResponseDataIfDuplicateRequest(storeType, key, differentBody));
		Optional<Object> responseDataIfDuplicateRequest = strategyLogic.getResponseDataIfDuplicateRequest(storeType, key, body);
		assertThat(responseDataIfDuplicateRequest.get()).isEqualTo(result);

	}

	@Test
	void preProceed() {
		String key = "preProceed";
		String body = "request body";
		strategyLogic.preProceed(storeType, key, body);

		Object o = requestStore.get(storeType, key);
		assertThat(o).isEqualTo(body);
	}

	@Test
	void postProceed() {
		String key = "postProceed";
		Object result = "result";
		strategyLogic.postProceed(storeType, key, result);

		Object o = responseStore.get(storeType, key);
		assertThat(o).isEqualTo(result);
	}

	@Test
	void exProceed() {
		String key = "exProceed";
		String body = "request body";
		strategyLogic.preProceed(storeType, key, body);

		Object o = requestStore.get(storeType, key);
		assertThat(o).isEqualTo(body);

		strategyLogic.exProceed(storeType, key);

		Object afterEx = responseStore.get(storeType, key);
		assertThat(afterEx).isNull();

	}
}