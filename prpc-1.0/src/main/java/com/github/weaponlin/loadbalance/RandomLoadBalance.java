package com.github.weaponlin.loadbalance;

import com.github.weaponlin.remote.URI;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {

    public static final String NAME = "Random";

    private Random random;

    public RandomLoadBalance() {
        this.random = new Random();
    }

    @Override
    public URI select(String service) {
        List<URI> providers = getProviders(service);

        return providers.get(random.nextInt(providers.size()));
    }
}
