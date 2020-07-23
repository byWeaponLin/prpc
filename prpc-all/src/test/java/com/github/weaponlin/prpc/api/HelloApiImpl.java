package com.github.weaponlin.prpc.api;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class HelloApiImpl implements HelloApi {

    private Random random = new Random();

    public HelloResponse hello(Long userId, HelloRequest request) {

        return HelloResponse.builder().greeting(RandomStringUtils.random(request.getSize(), true, true)).build();
    }
}
