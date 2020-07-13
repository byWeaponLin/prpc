package com.github.weaponlin.client;

import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.registry.Registry;
import com.github.weaponlin.registry.ZooKeeperRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.weaponlin.config.PRPCConfig.RegistryProperties;

public class PClient {

    private Set<Class<?>> services;

    private RegistryProperties discovery;

    private Map<Class<?>, Object> serviceInstances = new ConcurrentHashMap<>();

    private PRPCConfig prpcConfig;

    private PClient() {
    }

    private PClient(Set<Class<?>> services, String codec, String failStrategy,
                    String loadBalance, RegistryProperties discovery) {
        this.services = services;
        this.discovery = discovery;
        this.prpcConfig = new PRPCConfig().setCodec(codec)
                .setFailStrategy(failStrategy)
                .setLoadBalance(loadBalance);
    }

    public static PClientBuilder builder() {
        return new PClientBuilder();
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        if (!serviceInstances.containsKey(clazz)) {
            serviceInstances.putIfAbsent(clazz, PServiceFactory.getService(clazz, prpcConfig));
        }
        return (T) serviceInstances.get(clazz);
    }

    public PClient ready() {
        Registry registry = new ZooKeeperRegistry(Lists.newArrayList(services), discovery);
        registry.subscribe();
        return this;
    }

    public static class PClientBuilder {
        private Set<Class<?>> services = Sets.newHashSet();

        private String codec;

        private String failStrategy;

        private String loadBalance;

        private RegistryProperties discovery;

        public PClientBuilder services(List<Class<?>> services) {
            if (CollectionUtils.isNotEmpty(services)) {
                this.services.addAll(services);
            }
            return this;
        }

        public PClientBuilder services(Class<?>... services) {
            if (ArrayUtils.isNotEmpty(services)) {
                this.services.addAll(Arrays.asList(services));
            }
            return this;
        }

        public PClientBuilder codec(String codec) {
            this.codec = codec;
            return this;
        }

        public PClientBuilder failStrategy(String failStrategy) {
            this.failStrategy = failStrategy;
            return this;
        }

        public PClientBuilder loadBalance(String loadBalance) {
            this.loadBalance = loadBalance;
            return this;
        }

        public PClientBuilder discovery(RegistryProperties discovery) {
            this.discovery = discovery;
            return this;
        }

        public PClient build() {
            return new PClient(services, codec,
                    failStrategy, loadBalance, discovery);
        }
    }
}
