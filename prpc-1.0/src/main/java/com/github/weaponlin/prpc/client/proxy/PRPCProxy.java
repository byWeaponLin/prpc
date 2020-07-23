package com.github.weaponlin.prpc.client.proxy;


import com.github.weaponlin.prpc.client.PRequest;
import com.github.weaponlin.prpc.config.PRPCConfig;
import com.github.weaponlin.prpc.requestor.PClientRequestor;
import com.github.weaponlin.prpc.requestor.PRequestor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
public class PRPCProxy implements InvocationHandler {

    private Class<?> klass;

    private PRequestor pRequestor;

    public PRPCProxy(Class<?> klass, PRPCConfig prpcConfig) {
        this.klass = klass;
        this.pRequestor = new PClientRequestor(prpcConfig);
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
