package com.weaponlin.inf.prpc.api.hello;

import org.apache.commons.lang3.RandomStringUtils;

public class HelloApiImpl implements HelloApi {

    public HelloResponse hello(Long userId, HelloRequest request) {

        HelloResponse res = HelloResponse.builder().greeting(RandomStringUtils.random(request.getSize(), true, true))
                .build();
        System.out.println("invoke result: " + res);
        return res;
    }
}
