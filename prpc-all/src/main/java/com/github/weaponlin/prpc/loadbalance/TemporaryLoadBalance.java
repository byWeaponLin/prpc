package com.github.weaponlin.prpc.loadbalance;

import com.github.weaponlin.prpc.exception.PRpcException;
import com.github.weaponlin.prpc.remote.URI;

public class TemporaryLoadBalance implements LoadBalance {

    public static final String NAME = "temp";

    @Override
    public URI select(String service) {
        return getProviders(service).stream().findFirst()
                .orElseThrow(() -> new PRpcException("no providers"));
    }
}
