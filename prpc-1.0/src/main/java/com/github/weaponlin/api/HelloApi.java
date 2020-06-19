package com.github.weaponlin.api;

import com.github.weaponlin.inf.PRPCInterface;

public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
