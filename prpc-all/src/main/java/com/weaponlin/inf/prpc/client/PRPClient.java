package com.weaponlin.inf.prpc.client;

import com.google.common.collect.Lists;
import com.weaponlin.inf.prpc.codec.CodecType;
import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.registry.Registry;
import com.weaponlin.inf.prpc.registry.RegistryFactory;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PRPClient {

    private Map<Class<?>, Object> serviceInstances = new ConcurrentHashMap<>();

    private PRPConfig config;

    private Map<Class<?>, Registry> registryMap;

    private Map<Class<?>, PRPConfig.PGroup> serviceGroup;

    public PRPClient(PRPConfig config) {
        this.registryMap = new ConcurrentHashMap<>();
        this.serviceGroup = new ConcurrentHashMap<>();
        this.config = config;
        complementConfig();
        configValidate(config);
        init();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getService(@NonNull Class<T> service) {
        if (!serviceInstances.containsKey(service)) {
            final Registry registry = registryMap.get(service);
            registry.subscribe(service);
            serviceInstances.putIfAbsent(service, PServiceFactory.getService(service,
                    serviceGroup.get(service)));
        }
        return (T) serviceInstances.get(service);
    }

    private void init() {
        config.getGroups().forEach(group -> {
            Registry registry = RegistryFactory.createRegistry(group.getRegistryCenter(), Lists.newArrayList(group),
                    ProtocolType.prpc, 0);
            registry.subscribe();
            registry.getServices().forEach(service -> {
                registryMap.put(service, registry);
                serviceGroup.put(service, group);
                serviceInstances.putIfAbsent(service, PServiceFactory.getService(service, group));
            });
        });
    }

    private void complementConfig() {
        config.getGroups().forEach(group -> {
            if (group.getRegistryCenter() == null) {
                Optional.ofNullable(config.getRegistryCenter()).ifPresent(group::setRegistryCenter);
            }
            if (group.getConnectionTimeouts() <= 0) {
                Optional.ofNullable(config.getConnectionTimeout()).filter(e -> e <= 0)
                        .ifPresent(group::setConnectionTimeouts);
            }
            if (StringUtils.isBlank(group.getCodec()) || !CodecType.contain(group.getCodec())) {
                Optional.ofNullable(config.getCodec()).filter(StringUtils::isNotBlank)
                        .filter(CodecType::contain)
                        .ifPresent(group::setCodec);
            }

            if (StringUtils.isBlank(group.getProtocol()) || !ProtocolType.contain(group.getProtocol())) {
                Optional.ofNullable(config.getProtocol()).filter(StringUtils::isNotBlank)
                        .filter(ProtocolType::contain)
                        .ifPresent(group::setProtocol);
            }

            if (StringUtils.isBlank(group.getGroup())) {
                Optional.ofNullable(config.getGroup()).filter(StringUtils::isNotBlank)
                        .ifPresent(group::setGroup);
            }

            if (StringUtils.isBlank(group.getBasePackage())) {
                throw new PRPCException("service base package is blank, please check it");
            }
        });
    }

    /**
     * TODO refactor, consider
     *
     * @param config
     */
    private void configValidate(PRPConfig config) {
        // TODO
    }
}
