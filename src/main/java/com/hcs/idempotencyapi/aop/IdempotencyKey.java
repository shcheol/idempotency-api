package com.hcs.idempotencyapi.aop;

import com.hcs.idempotencyapi.ex.IdempotentError;
import com.hcs.idempotencyapi.ex.IdempotentException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public record IdempotencyKey(String key, boolean isRequired) {

    boolean isValid(String regex) {
        if (!StringUtils.hasText(key)) {
            if (isRequired()) {
                throw new IdempotentException(IdempotentError.BAD_REQUEST);
            }
            throw new JoinProceedException();
        }

        if (!StringUtils.hasText(key)) {
            throw new IdempotentException(IdempotentError.NO_KEY);
        }

        if (StringUtils.hasText(regex)) {
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(key).matches()) {
                throw new IdempotentException(IdempotentError.INVALID_KEY);
            }
        }
        return true;
    }
}
