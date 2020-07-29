package com.github.weaponlin.prpc.api;

import org.apache.commons.lang3.RandomStringUtils;

public class HelloApiImpl implements HelloApi {

    public HelloResponse hello(Long userId, HelloRequest request) {

        return HelloResponse.builder().greeting(RandomStringUtils.random(request.getSize(), true, true))
                .build();
    }
}
