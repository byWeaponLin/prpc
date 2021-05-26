package com.weaponlin.inf.prpc.sprint.boot.starter.api;

public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
