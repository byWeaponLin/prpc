package com.weaponlin.inf.prpc.api;

public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
