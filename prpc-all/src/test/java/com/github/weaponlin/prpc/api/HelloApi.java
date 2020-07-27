package com.github.weaponlin.prpc.api;

import com.github.weaponlin.prpc.annotation.PRPC;

@PRPC(group = "test")
public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
