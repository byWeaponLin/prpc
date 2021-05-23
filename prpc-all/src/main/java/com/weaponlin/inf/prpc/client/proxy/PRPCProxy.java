package com.weaponlin.inf.prpc.client.proxy;


import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.requestor.PClientRequestor;
import com.weaponlin.inf.prpc.requestor.PRequestor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
public class PRPCProxy implements InvocationHandler {

    private Class<?> clazz;

    private PRequestor pRequestor;

    private PRPConfig.PGroup group;

    public PRPCProxy(Class<?> clazz, PRPConfig.PGroup group) {
        this.clazz = clazz;
        this.group = group;
        this.pRequestor = new PClientRequestor(group);
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        String groupName = this.group.getGroup();
        String requestId = UUID.randomUUID().toString();
        final PRequest request = PRequest.builder()
                .requestId(requestId)
                .serviceName(clazz.getName())
                .methodName(method.getName())
                .group(groupName)
                .params(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        return pRequestor.request(request);
    }
}
