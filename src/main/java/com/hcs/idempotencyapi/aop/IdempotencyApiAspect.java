package com.hcs.idempotencyapi.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcs.idempotencyapi.ex.IdempotentError;
import com.hcs.idempotencyapi.ex.IdempotentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class IdempotencyApiAspect {

    private final IdempotencyApiStrategy strategy;
	private final ObjectMapper om;

    public IdempotencyApiAspect(IdempotencyApiStrategy strategy, ObjectMapper om) {
        this.strategy = strategy;
		this.om = om;
    }

    @Around("@annotation(idempotencyApi)")
    public Object join(ProceedingJoinPoint joinPoint, IdempotencyApi idempotencyApi) throws Throwable {

        if (!validAnnotationLocation(joinPoint)) {
            log.warn("invalid location, idempotencyApi Annotation does not work");
            return joinPoint.proceed();
        }

        String storeType = idempotencyApi.storeType();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String idempotencyKey = getIdempotencyKey(request, idempotencyApi);
        if (!StringUtils.hasText(idempotencyKey)) {
            if (idempotencyApi.keyRequired()) {
                throw new IdempotentException(IdempotentError.BAD_REQUEST);
            }
            return joinPoint.proceed();
        }
        strategy.isValidKey(idempotencyKey, idempotencyApi.keyPatternRegex());

        String body = getBody(request);

        Optional<Object> duplicatedRequest = strategy.getResponseDataIfDuplicateRequest(storeType, idempotencyKey, body);
		return ResponseEntity.ok(duplicatedRequest.orElseGet(
				() -> {
					try {
						log.error("+_+_+_+_+_+_+_+_+_+_+");
						strategy.preProceed(storeType, idempotencyKey, body);
						Object proceed = joinPoint.proceed();
						if (proceed instanceof ResponseEntity<?> response){
							proceed = response.getBody();
						}
						strategy.postProceed(storeType, idempotencyKey, proceed);
						return proceed;
					} catch (Throwable e) {
						strategy.exProceed(storeType, idempotencyKey);
						throw new RuntimeException(e);
					}
				}
		));

	}

    private boolean validAnnotationLocation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[] annotations = signature.getMethod().getAnnotations();

        return Arrays.stream(annotations).anyMatch(a -> {
            if (a instanceof PostMapping || a instanceof PutMapping) return true;
            if (a instanceof RequestMapping requestMapping) {
                RequestMethod[] method = requestMapping.method();
                return Arrays.stream(method).anyMatch(rm -> rm.equals(RequestMethod.POST) || rm.equals(RequestMethod.PUT));
            }
            return false;
        });
    }

    private String getIdempotencyKey(HttpServletRequest request, IdempotencyApi idempotencyApi) {
        return request.getHeader(idempotencyApi.headerKey());
    }
    private String getBody(HttpServletRequest request) throws IOException {
		String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
		try {
			return om.writeValueAsString(om.readValue(requestBody, new TypeReference<Object>() {
			}));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
    }

}
