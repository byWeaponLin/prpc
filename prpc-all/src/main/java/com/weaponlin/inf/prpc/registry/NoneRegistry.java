package com.weaponlin.inf.prpc.registry;

import com.weaponlin.inf.prpc.annotation.PRPC;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.remote.URI;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class NoneRegistry extends AbstractRegistry {

    public static final String REGISTRY = "none";

    private Map<String, List<Class<?>>> groupService = new ConcurrentHashMap<>();

    private PConfig config;

    public NoneRegistry(PConfig config) {
        this.config = config;
    }

    @Override
    public void register() {
        throw new PRPCException("not support operation");
    }

    @Override
    public void register(Class<?> service) {
//        throw new PRPCException("not support operation");
    }

    @Override
    public void unregister() {
        return;
    }

    @Override
    public void subscribe() {
        throw new PRPCException("not support operation");
    }

    @Override
    public void subscribe(Class<?> service) {
        PRPC prpc = service.getAnnotation(PRPC.class);
        String group = prpc.group();
        if (!groupService.containsKey(group)) {
            groupService.put(group, Lists.newArrayList(service));
        } else {
            groupService.get(group).add(service);
        }
        try {
            refresh();
        } catch (Exception e) {
            log.error("zk watch failed", e);
        }
    }

    @Override
    public void unsubscribe() {
        throw new PRPCException("not support operation");
    }

    @Override
    public void nodeChanged() {
        throw new PRPCException("not support operation");
    }

    @Override
    public void refresh() {
        try {
            // discovery service if config
            if (MapUtils.isNotEmpty(groupService)) {
                groupService.forEach((group, services) -> {
                    if (CollectionUtils.isEmpty(services)) {
                        return;
                    }
                    services.stream().filter(Objects::nonNull).forEach(service -> {
                        try {
                            // discovery services
                            discoverService(service.getName() + ":" + group, Lists.newArrayList(config.getAddress()));
                        } catch (Exception e) {
                            log.error("discovery service failed, service: {}, group: {}", service.getName(), group);
                        }
                    });
                });
            }
        } catch (Exception e) {
            log.error("zk watch failed", e);
            throw new PRPCException("zk watch failed", e);
        }
    }

    private void discoverService(String service, List<String> serverPath) {
        if (CollectionUtils.isEmpty(serverPath)) {
            return;
        }
        final Set<URI> uris = serverPath.stream().map(URI::newURI)
                .collect(toSet());
        addProvider(service, uris);
    }
}