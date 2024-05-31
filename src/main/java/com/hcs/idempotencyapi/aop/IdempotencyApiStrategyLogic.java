package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.ex.IdempotentError;
import com.hcs.idempotencyapi.ex.IdempotentException;
import com.hcs.idempotencyapi.repository.mapper.RequestStoreMapper;
import com.hcs.idempotencyapi.repository.mapper.ResponseStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IdempotencyApiStrategyLogic implements IdempotencyApiStrategy {

	private final RequestStoreMapper requestStore;
	private final ResponseStoreMapper responseStore;

	public IdempotencyApiStrategyLogic(RequestStoreMapper requestStore, ResponseStoreMapper responseStore) {
		this.requestStore = requestStore;
		this.responseStore = responseStore;
	}

	@Override
	public void isValidKey(String key, String regex) {
		if (!StringUtils.hasText(key)) {
			throw new IdempotentException(IdempotentError.NO_KEY);
		}

		if (StringUtils.hasText(regex)) {
			Pattern pattern = Pattern.compile(regex);
			if (!pattern.matcher(key).matches()) {
				throw new IdempotentException(IdempotentError.INVALID_KEY);
			}
		}
	}

	@Override
	public Optional<Object> getResponseDataIfDuplicateRequest(String storeType, String key, String requestBody) {
		if (requestStore.has(storeType, key)) {
			if (!hasSamePayload(requestBody, requestStore.get(storeType, key))) {
				throw new IdempotentException(IdempotentError.UNPROCESSABLE_ENTITY);
			}
			if (!responseStore.has(storeType, key)) {
				throw new IdempotentException(IdempotentError.CONFLICT);
			}

			return Optional.of(responseStore.get(storeType, key));
		}
		return Optional.empty();
	}

	private boolean hasSamePayload(String otherRequest, Object firstRequest) {
		log.debug("duplicate request {}, first request {}", otherRequest, firstRequest);
		return otherRequest.equals(firstRequest);
	}

	@Override
	public void preProceed(String storeType, String key, String requestBody) {
		requestStore.set(storeType, key, requestBody);
	}

	@Override
	public void postProceed(String storeType, String key, Object result) {
		responseStore.set(storeType, key, result);
	}

	@Override
	public void exProceed(String storeType, String key) {
		requestStore.remove(storeType, key);
	}
}
