package com.hcs.idempotencyapi.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class IdempotencyApiAspect {

	private final PrepareIdempotentTemplate template;


	public IdempotencyApiAspect(PrepareIdempotentTemplate template) {
		this.template = template;
	}

	@Around("@annotation(idempotencyApi)")
	public Object join(ProceedingJoinPoint joinPoint, IdempotencyApi idempotencyApi) throws Throwable {

		return template.prepareAndDoProcess(joinPoint, idempotencyApi);
	}

}
