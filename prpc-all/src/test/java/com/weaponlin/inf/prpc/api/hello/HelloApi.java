package com.weaponlin.inf.prpc.api.hello;

public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
