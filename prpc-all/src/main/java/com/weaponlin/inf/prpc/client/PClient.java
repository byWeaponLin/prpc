package com.weaponlin.inf.prpc.client;

import com.weaponlin.inf.prpc.annotation.PRPC;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.registry.Registry;
import com.weaponlin.inf.prpc.registry.RegistryFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PClient {

    private Map<Class<?>, Object> serviceInstances = new ConcurrentHashMap<>();

    private PConfig config;

    private Map<String, GroupRegistry> groupRegistry;

    private Map<String, Registry> registryMap;

    public PClient(PConfig config) {
        this.groupRegistry = new ConcurrentHashMap<>();
        this.registryMap = new ConcurrentHashMap<>();
        configValidate(config);
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getService(@NonNull Class<T> service) {
        final PRPC prpc = service.getAnnotation(PRPC.class);
        if (prpc == null || StringUtils.isBlank(prpc.group())) {
            throw new PRPCException("class must annotate with @PRPC or group cant be blank");
        }

        if (!serviceInstances.containsKey(service)) {
            final Registry registry = getRegistry(prpc.group());
            registry.subscribe(service);
            serviceInstances.putIfAbsent(service, PServiceFactory.getService(service, config));
        }
        return (T) serviceInstances.get(service);
    }

    private Registry getRegistry(String group) {
        if (!groupRegistry.containsKey(group) && StringUtils.isBlank(config.getAddress())) {
            throw new PRPCException("cant find zookeeper for group " + group);
        }
        groupRegistry.putIfAbsent(group, new GroupRegistry(config.getAddress()));
        GroupRegistry groupRegistry = this.groupRegistry.get(group);
        final String registryAddress = groupRegistry.getAddress();
        if (registryMap.containsKey(registryAddress)) {
            return registryMap.get(registryAddress);
        } else {
            Registry registry = RegistryFactory.createRegistry(config, 0);
            registryMap.put(registryAddress, registry);
            return registry;
        }
    }

    /**
     * TODO refactor, consider
     *
     * @param config
     */
    private void configValidate(PConfig config) {
        if (config == null) {
            throw new PRPCException("config cant be null");
        }

        if (StringUtils.isBlank(config.getAddress())) {

            if (CollectionUtils.isEmpty(config.getGroups())) {
                throw new PRPCException("no valid zookeeper configuration");
            }

            config.getGroups().stream().filter(Objects::nonNull).forEach(group -> {
                Optional.ofNullable(group.getGroup()).filter(StringUtils::isNotBlank)
                        .orElseThrow(() -> new PRPCException("group is invalid for it is blank"));
                Optional.ofNullable(group.getAddress()).filter(StringUtils::isNotBlank)
                        .orElseThrow(() -> new PRPCException("invalid zookeeper configuration"));
                groupRegistry.putIfAbsent(group.getGroup(), new GroupRegistry(group.getAddress()));
            });
        } else {
            if (CollectionUtils.isEmpty(config.getGroups())) {
                return;
            }
            config.getGroups().stream().filter(Objects::nonNull).forEach(group -> {
                Optional.ofNullable(group.getGroup()).filter(StringUtils::isNotBlank)
                        .orElseThrow(() -> new PRPCException("group is invalid for it is blank"));
                if (StringUtils.isNotBlank(group.getAddress())) {
                    groupRegistry.putIfAbsent(group.getGroup(), new GroupRegistry(group.getAddress()));
                } else {
                    groupRegistry.putIfAbsent(group.getGroup(), new GroupRegistry(config.getAddress()));
                }
            });
        }
    }

    @Getter
    @Setter
    public static class GroupRegistry {
        private String address;

        public GroupRegistry(String address) {
            this.address = address;
        }
    }
}
