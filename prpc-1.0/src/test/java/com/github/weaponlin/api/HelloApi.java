package com.github.weaponlin.api;

public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
