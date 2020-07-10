package com.github.weaponlin.client.proxy;


import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.codec.PEncoder;
import com.github.weaponlin.requestor.PClientRequestor;
import com.github.weaponlin.requestor.PRequestor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
public class PRPCProxy implements InvocationHandler {

    private Class<?> klass;

    private PRequestor pRequestor;

    public PRPCProxy(Class<?> klass) {
        this.klass = klass;
        this.pRequestor = new PClientRequestor();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final PRequest request = PRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .serviceName(klass.getName())
                .methodName(method.getName())
                .params(args)
                .build();
        return pRequestor.request(request);
    }
}
