package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.aop.vo.IdempotencyKey;
import com.hcs.idempotencyapi.aop.vo.IdempotencyRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@Slf4j
@Component
public class PrepareIdempotentTemplate {

	private final IdempotentTemplate template;
	private final IdempotencyRequestExtractor requestExtractor;

	public PrepareIdempotentTemplate(IdempotentTemplate template, IdempotencyRequestExtractor requestExtractor) {
		this.template = template;
		this.requestExtractor = requestExtractor;
	}

	public Object prepareAndDoProcess(ProceedingJoinPoint joinPoint, IdempotencyApi idempotencyApi) throws Throwable {

		IdempotencyRequest idempotencyRequest = requestExtractor.prepare(idempotencyApi);

		IdempotencyKey key = idempotencyRequest.key();

		if (!validAnnotationLocation(joinPoint)) {
			log.warn("invalid location, idempotencyApi Annotation does not work");
			return joinPoint.proceed();
		}

		boolean keyRequired = idempotencyRequest.idempotencyApi().keyRequired();
		if (key.isEmpty() && !keyRequired) {
			return joinPoint.proceed();
		}
		key.isValid(idempotencyRequest.idempotencyApi().keyPatternRegex(), keyRequired);

		String storeType = idempotencyRequest.idempotencyApi().storeType();
		String idempotencyKey = key.key();
		String body = idempotencyRequest.body();
		return template.doProcess(joinPoint, storeType, idempotencyKey, body);
	}

	private boolean validAnnotationLocation(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Annotation[] annotations = signature.getMethod().getAnnotations();

		return Arrays.stream(annotations).anyMatch(a -> {
			if (a instanceof PostMapping || a instanceof PutMapping) return true;
			if (a instanceof RequestMapping) {
				RequestMethod[] method = ((RequestMapping) a).method();
				return Arrays.stream(method).anyMatch(rm -> rm.equals(RequestMethod.POST) || rm.equals(RequestMethod.PUT));
			}
			return false;
		});
	}
}
