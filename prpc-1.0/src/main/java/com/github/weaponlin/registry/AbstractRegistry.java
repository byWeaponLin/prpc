package com.github.weaponlin.registry;

import com.github.weaponlin.remote.URI;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegistry implements Registry {

    /**
     * TODO V: use list
     */
    static Map<String, Set<URI>> services = new ConcurrentHashMap<>();


    protected void serviceCreated(String service, URI uri) {

    }

    protected void serviceDeleted(String service, URI uri) {

    }

    protected void serviceChanged(String service, URI uri) {

    }

    public static Set<URI> getProviders(String service) {
        return services.get(service);
    }

    protected void addProvider(String service, URI uri) {
        if (services.containsKey(service)) {
            services.get(service).add(uri);
        } else {
            // TODO maybe need lock
            Set<URI> uris = Sets.newHashSet(uri);
            services.put(service, uris);
        }
    }
}
