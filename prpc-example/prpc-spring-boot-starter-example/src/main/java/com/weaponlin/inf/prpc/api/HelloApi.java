package com.weaponlin.inf.prpc.api;

import com.weaponlin.inf.prpc.spring.boot.starter.annotation.ExportService;

@ExportService
public interface HelloApi {

    HelloResponse hello(Long userId, HelloRequest request);
}
