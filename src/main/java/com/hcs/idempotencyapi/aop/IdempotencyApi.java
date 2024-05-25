package com.hcs.idempotencyapi.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdempotencyApi {

	String headerKey() default "idempotent";

	boolean keyRequired() default false;

	String keyPatternRegex() default "";
	String storeType() default "inMemoryIdempotencyKeyStore";
}
