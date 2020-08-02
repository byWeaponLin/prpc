package com.github.weaponlin.prpc.registry;

import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.exception.PRpcException;

public class RegistryFactory {

    public static Registry createRegistry(PConfig config, int port) {
        // TODO validate config here
        if (NoneRegistry.REGISTRY.equals(config.getRegistry())) {
            return new NoneRegistry(config);
        } else if (ZooKeeperRegistry.REGISTRY.equals(config.getRegistry())) {
            return new ZooKeeperRegistry(port, config.getAddress(), config.getConnectionTimeout());
        }

        throw new PRpcException(String.format("invalid registry: %s, optional values is: %s, %s",
                config.getRegistry(), NoneRegistry.REGISTRY, ZooKeeperRegistry.REGISTRY));
    }
}
