package com.weaponlin.inf.prpc.client.proxy;


import com.weaponlin.inf.prpc.annotation.PRPC;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.requestor.PClientRequestor;
import com.weaponlin.inf.prpc.requestor.PRequestor;
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
                .orElseThrow(() -> new PRPCException("api class must annotate with @PRPC"));
        final PRequest request = PRequest.builder()
                .requestId("1111")
                .serviceName(clazz.getName())
                .methodName(method.getName())
                .group(group)
                .params(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        return pRequestor.request(request);
    }
}
