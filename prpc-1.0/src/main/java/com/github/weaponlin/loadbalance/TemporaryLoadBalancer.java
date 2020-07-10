package com.github.weaponlin.loadbalance;

import com.github.weaponlin.remote.URI;

public class TemporaryLoadBalancer implements LoadBalancer {

    @Override
    public URI select() {
        return URI.newURI("127.0.0.1", 8888);
    }
}
