package com.weaponlin.inf.prpc.registry;

import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;

public class RegistryFactory {

    public static Registry createRegistry(PConfig config, int port) {
        // TODO validate config here
        if (NoneRegistry.REGISTRY.equals(config.getRegistry())) {
            return new NoneRegistry(config);
        } else if (PRPCZooKeeperRegistry.REGISTRY.equals(config.getRegistry())) {
            return new PRPCZooKeeperRegistry(port, config.getAddress(), config.getConnectionTimeout());
        }

        throw new PRPCException(String.format("invalid registry: %s, optional values is: %s, %s",
                config.getRegistry(), NoneRegistry.REGISTRY, PRPCZooKeeperRegistry.REGISTRY));
    }

    public static Registry createRegistry(String naming, String address, int port) {
        if (NoneRegistry.REGISTRY.equals(naming)) {
            // TODO
            return new NoneRegistry(null);
        } else if (PRPCZooKeeperRegistry.REGISTRY.equals(naming)) {
            return new PRPCZooKeeperRegistry(port, address, 30000);
        }

        throw new PRPCException(String.format("invalid registry: %s, optional values is: %s, %s",
                naming, NoneRegistry.REGISTRY, PRPCZooKeeperRegistry.REGISTRY));
    }
}
