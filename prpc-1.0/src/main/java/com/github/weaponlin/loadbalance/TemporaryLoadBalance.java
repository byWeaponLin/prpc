package com.github.weaponlin.loadbalance;

import com.github.weaponlin.exception.PRpcException;
import com.github.weaponlin.remote.URI;

public class TemporaryLoadBalance implements LoadBalance {

    public static final String NAME = "temp";

    @Override
    public URI select(String service) {
        return getProviders(service).stream().findFirst()
                .orElseThrow(() -> new PRpcException("no providers"));
    }
}
