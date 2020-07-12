package com.github.weaponlin.loadbalance;

import com.github.weaponlin.exception.PRpcException;
import com.github.weaponlin.registry.AbstractRegistry;
import com.github.weaponlin.remote.URI;

import java.util.Set;

public class TemporaryLoadBalancer implements LoadBalancer {

    @Override
    public URI select(String service) {
        Set<URI> providers = AbstractRegistry.getProviders(service);
        return providers.stream().findFirst()
                .orElseThrow(() -> new PRpcException("no providers"));
    }
}
