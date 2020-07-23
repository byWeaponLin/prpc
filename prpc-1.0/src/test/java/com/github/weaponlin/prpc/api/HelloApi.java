package com.github.weaponlin.prpc.api;

public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
