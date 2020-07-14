package com.github.weaponlin.loadbalance;

import com.github.weaponlin.remote.URI;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance implements LoadBalance {

    public static final String NAME = "RoundRobin";

    private Map<String, AtomicInteger> referenceCount;

    public RoundRobinLoadBalance() {
        this.referenceCount = new ConcurrentHashMap<>();
    }

    @Override
    public URI select(String service) {
        if (!referenceCount.containsKey(service)) {
            referenceCount.putIfAbsent(service, new AtomicInteger());
        }
        AtomicInteger atomicInteger = referenceCount.get(service);

        List<URI> providers = getProviders(service);
        return providers.get(Math.abs(atomicInteger.getAndIncrement() % providers.size()));
    }
}
