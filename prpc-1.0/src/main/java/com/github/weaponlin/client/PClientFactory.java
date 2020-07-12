package com.github.weaponlin.client;

import com.github.weaponlin.client.proxy.PRPCProxy;
import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.registry.Registry;
import com.github.weaponlin.registry.ZooKeeperRegistry;
import com.google.common.collect.Lists;

import java.lang.reflect.Proxy;

public class PClientFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> service) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{service}, new PRPCProxy(service));
    }

    public static <T> T getService(Class<T> service, PRPCConfig.RegistryProperties registryProperties) {
        Registry registry = new ZooKeeperRegistry(8888, Lists.newArrayList(service), registryProperties);
        registry.subscribe();
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{service}, new PRPCProxy(service));
    }
}
