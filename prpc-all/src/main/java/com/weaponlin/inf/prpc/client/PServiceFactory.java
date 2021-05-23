package com.weaponlin.inf.prpc.client;

import com.weaponlin.inf.prpc.client.proxy.PRPCProxy;
import com.weaponlin.inf.prpc.config.PRPConfig;

import java.lang.reflect.Proxy;

public class PServiceFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> service, PRPConfig.PGroup group) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{service}, new PRPCProxy(service, group));
    }
}
