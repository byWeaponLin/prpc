package com.weaponlin.inf.prpc.registry.none;

import com.weaponlin.inf.prpc.registry.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NoneRegistry extends AbstractRegistry {

    public static final String REGISTRY = "none";

    private Map<String, List<Class<?>>> groupService = new ConcurrentHashMap<>();

    public NoneRegistry() {
    }

    @Override
    public void register() {

    }

    @Override
    public void register(Class<?> service) {

    }

    @Override
    public void unregister() {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void subscribe(Class<?> service) {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public List<Class<?>> getServices() {
        return Collections.emptyList();
    }

    @Override
    public void nodeChanged() {

    }

    @Override
    public void refresh() {

    }
}
