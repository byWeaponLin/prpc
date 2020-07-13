package com.github.weaponlin.client;

import com.github.weaponlin.client.proxy.PRPCProxy;
import com.github.weaponlin.config.PRPCConfig;

import java.lang.reflect.Proxy;

public class PServiceFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> service, PRPCConfig prpcConfig) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{service}, new PRPCProxy(service, prpcConfig));
    }
}
