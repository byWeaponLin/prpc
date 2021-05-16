package com.weaponlin.inf.prpc.api;

import com.weaponlin.inf.prpc.annotation.PRPC;

@PRPC()
public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
