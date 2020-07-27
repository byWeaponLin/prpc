package com.github.weaponlin.prpc.client.proxy;


import com.github.weaponlin.prpc.annotation.PRPC;
import com.github.weaponlin.prpc.client.PRequest;
import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.exception.PRpcException;
import com.github.weaponlin.prpc.requestor.PClientRequestor;
import com.github.weaponlin.prpc.requestor.PRequestor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PRPCProxy implements InvocationHandler {

    private Class<?> clazz;

    private PRequestor pRequestor;

    public PRPCProxy(Class<?> clazz, PConfig config) {
        this.clazz = clazz;
        this.pRequestor = new PClientRequestor(config);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String group = Optional.of(clazz.getAnnotation(PRPC.class)).map(PRPC::group).filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new PRpcException("api class must annotate with @PRPC"));
        final PRequest request = PRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .serviceName(clazz.getName())
                .methodName(method.getName())
                .group(group)
                .params(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        return pRequestor.request(request);
    }
}
