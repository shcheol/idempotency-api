package com.hcs.idempotencyapi;

import com.hcs.idempotencyapi.aop.IdempotencyApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DemoController {

    @IdempotencyApi
    @GetMapping("/test")
    public ResponseEntity<String> get(){
        System.out.println("DemoController.test");
        return ResponseEntity.ok("DemoController");
    }

    @IdempotencyApi
    @PostMapping("/test")
    public ResponseEntity<TestClass> test(@RequestBody TestClass testClass){
        System.out.println("DemoController.test");
        return ResponseEntity.ok(testClass);
    }

    @IdempotencyApi
    @PostMapping("/throw")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TestClass> throwMethod(@RequestBody TestClass testClass){
        System.out.println("DemoController.test");
        throw new RuntimeException();
    }
}
