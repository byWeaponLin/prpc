package com.github.weaponlin.client;

import com.github.weaponlin.client.proxy.PRPCProxy;

import java.lang.reflect.Proxy;

public class PClientFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> service) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{service}, new PRPCProxy(service));
    }
}
