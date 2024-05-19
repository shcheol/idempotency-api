package com.hcs.idempotencyapi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdempotencyApiAspectTest {

    @Autowired
    IdempotencyApiAspect aspect;

    @Test
    void join() {
//        aspect.join(new MethodInvocationProceedingJoinPoint() {
//        });

    }
}