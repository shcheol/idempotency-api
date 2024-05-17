package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.ex.IdempotentError;
import com.hcs.idempotencyapi.ex.IdempotentException;
import com.hcs.idempotencyapi.repository.IdempotencyKeyStore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Aspect
@Component
public class IdempotencyApiAspect {
	private final IdempotencyKeyStore idempotencyKeyStore;

	public IdempotencyApiAspect(IdempotencyKeyStore idempotencyKeyStore) {
		this.idempotencyKeyStore = idempotencyKeyStore;
	}

	@Around("@annotation(idempotencyApi)")
	public Object join(ProceedingJoinPoint joinPoint, IdempotencyApi idempotencyApi) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String idempotencyKey = request.getHeader(idempotencyApi.headerKey());
		String body = getBody(request);

		String name = joinPoint.getSignature().getName();

		String REQUEST_PREFIX = "REQUEST_"+name;
		String RESPONSE_PREFIX = "RESPONSE_"+name;

		if (!idempotencyKeyStore.has(REQUEST_PREFIX,idempotencyKey)){
			idempotencyKeyStore.set(REQUEST_PREFIX, idempotencyKey, body);
		} else {
			Object o = idempotencyKeyStore.get(REQUEST_PREFIX, idempotencyKey);
			if (!o.equals(body)){
				throw new IdempotentException(IdempotentError.UNPROCESSABLE_ENTITY);
			}
			if (!idempotencyKeyStore.has(RESPONSE_PREFIX, idempotencyKey)){
				throw new IdempotentException(IdempotentError.CONFLICT);
			}
		}

		if (idempotencyApi.keyRequired() && !StringUtils.hasText(idempotencyKey)) {
			throw new IdempotentException(IdempotentError.BAD_REQUEST);
		}

		if (StringUtils.hasText(idempotencyKey) && idempotencyKeyStore.has(RESPONSE_PREFIX, idempotencyKey)) {
			return idempotencyKeyStore.get(RESPONSE_PREFIX, idempotencyKey);
		}

		Object proceed = joinPoint.proceed();
		if (StringUtils.hasText(idempotencyKey)) {
			idempotencyKeyStore.set(RESPONSE_PREFIX, idempotencyKey, proceed);
		}
		return proceed;
	}
	private String getBody(HttpServletRequest request) throws IOException {
		return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
	}
}
