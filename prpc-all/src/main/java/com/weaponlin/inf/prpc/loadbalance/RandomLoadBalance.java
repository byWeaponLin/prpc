package com.weaponlin.inf.prpc.loadbalance;

import com.weaponlin.inf.prpc.loader.Extension;
import com.weaponlin.inf.prpc.remote.URI;

import java.util.List;
import java.util.Random;

@Extension(name = "random")
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
