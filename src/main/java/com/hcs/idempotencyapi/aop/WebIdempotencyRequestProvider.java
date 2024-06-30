package com.hcs.idempotencyapi.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;

@Component
public class WebIdempotencyRequestProvider implements IdempotencyRequestProvider {
    @Override
    public IdempotencyRequest prepare(IdempotencyApi idempotencyApi) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String value = request.getHeader(idempotencyApi.findKey());
        IdempotencyKey idempotencyKey = new IdempotencyKey(value, idempotencyApi.keyRequired());
        String body = getBody(request);

        return new IdempotencyRequest(idempotencyKey, body);
    }

    private String getBody(HttpServletRequest request) {
        try {
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
