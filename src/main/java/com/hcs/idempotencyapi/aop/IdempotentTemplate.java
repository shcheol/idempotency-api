package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.ex.IdempotentError;
import com.hcs.idempotencyapi.ex.IdempotentException;
import com.hcs.idempotencyapi.repository.mapper.RequestStoreMapper;
import com.hcs.idempotencyapi.repository.mapper.ResponseStoreMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IdempotentTemplate {

	private final RequestStoreMapper requestStore;
	private final ResponseStoreMapper responseStore;

	public IdempotentTemplate(RequestStoreMapper requestStore, ResponseStoreMapper responseStore) {
		this.requestStore = requestStore;
		this.responseStore = responseStore;
	}

	public Object doProcess(ProceedingJoinPoint joinPoint, String storeType, String idempotencyKey, String body) {
		Optional<Object> duplicatedRequest = getResponseDataIfDuplicateRequest(storeType, idempotencyKey, body);
		return duplicatedRequest.orElseGet(() -> {
			try {
				preProceed(storeType, idempotencyKey, body);
				Object proceed = joinPoint.proceed();
				postProceed(storeType, idempotencyKey, proceed);
				return proceed;
			} catch (Throwable e) {
				exProceed(storeType, idempotencyKey);
				throw new RuntimeException(e);
			}
		});
	}


	public Optional<Object> getResponseDataIfDuplicateRequest(String storeType, String key, String requestBody) {
		if (!requestStore.has(storeType, key)) {
			return Optional.empty();
		}

		if (!requestBody.equals(requestStore.get(storeType, key))) {
			throw new IdempotentException(IdempotentError.UNPROCESSABLE_ENTITY);
		}
		if (!responseStore.has(storeType, key)) {
			throw new IdempotentException(IdempotentError.CONFLICT);
		}
		return Optional.of(responseStore.get(storeType, key));
	}

	public void preProceed(String storeType, String key, String requestBody) {
		requestStore.set(storeType, key, requestBody);
	}

	public void postProceed(String storeType, String key, Object result) {
		responseStore.set(storeType, key, result);
	}

	public void exProceed(String storeType, String key) {
		requestStore.remove(storeType, key);
	}
}
