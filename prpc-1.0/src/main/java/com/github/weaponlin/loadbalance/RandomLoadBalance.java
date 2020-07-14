package com.github.weaponlin.loadbalance;

import com.github.weaponlin.remote.URI;

public class RandomLoadBalance implements LoadBalance {

    public static final String NAME = "Random";

    @Override
    public URI select(String service) {
        // TODO
        return null;
    }
}
