package com.github.weaponlin.registry;

import com.github.weaponlin.remote.URI;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractRegistry implements Registry {

    private Map<String, Set<URI>> services = new ConcurrentHashMap<>();


    protected void serviceCreated(String service, URI uri) {

    }

    protected void serviceDeleted(String service, URI uri) {

    }

    protected void serviceChanged(String service, URI uri) {

    }
}
