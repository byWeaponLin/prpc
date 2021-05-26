package com.weaponlin.inf.prpc.controller;

import com.weaponlin.inf.prpc.api.HelloApi;
import com.weaponlin.inf.prpc.api.HelloRequest;
import com.weaponlin.inf.prpc.api.HelloResponse;
import com.weaponlin.inf.prpc.spring.boot.starter.annotation.ReferenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class HelloController {

    @ReferenceService
    private HelloApi helloApi;

    @GetMapping("/hello")
    public HelloResponse hello() {
        HelloRequest req = new HelloRequest();
        req.setSize(111);
        req.setMessage("asdfasdfadsf");
        return helloApi.hello(1L, req);
    }
}
