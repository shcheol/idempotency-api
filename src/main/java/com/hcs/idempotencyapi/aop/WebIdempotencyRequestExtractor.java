package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.aop.vo.IdempotencyRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;

@Component
public class WebIdempotencyRequestExtractor implements IdempotencyRequestExtractor {
    @Override
    public IdempotencyRequest prepare(IdempotencyApi idempotencyApi) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String key = getKey(idempotencyApi, request);
        String body = getBody(request);
        return IdempotencyRequest.create(key, body, idempotencyApi);
    }

	private String getKey(IdempotencyApi idempotencyApi, HttpServletRequest request) {
		return request.getHeader(idempotencyApi.findKey());
	}

	private String getBody(HttpServletRequest request) {
        try {
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
