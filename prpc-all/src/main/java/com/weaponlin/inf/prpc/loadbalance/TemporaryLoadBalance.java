package com.weaponlin.inf.prpc.loadbalance;

import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.loader.Extension;
import com.weaponlin.inf.prpc.remote.URI;

@Extension(name = "temp")
public class TemporaryLoadBalance implements LoadBalance {

    public static final String NAME = "temp";

    @Override
    public URI select(String service) {
        return getProviders(service).stream().findFirst()
                .orElseThrow(() -> new PRpcException("no providers"));
    }
}
