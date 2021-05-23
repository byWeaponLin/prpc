package com.weaponlin.inf.prpc.registry;

import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.registry.none.NoneRegistry;
import com.weaponlin.inf.prpc.registry.zookeeper.DubboZooKeeperRegistry;
import com.weaponlin.inf.prpc.registry.zookeeper.PRPC2ZooKeeperRegistry;
import com.weaponlin.inf.prpc.registry.zookeeper.PRPCZooKeeperRegistry;

import java.util.List;

public class RegistryFactory {

    public static Registry createRegistry(PConfig config, int port) {
        // TODO validate config here
        if (NoneRegistry.REGISTRY.equals(config.getRegistry())) {
            return new NoneRegistry(config);
        } else if (PRPC2ZooKeeperRegistry.REGISTRY.equals(config.getRegistry())) {
            return new PRPCZooKeeperRegistry(port, config.getAddress(), config.getConnectionTimeout());
        }

        throw new PRPCException(String.format("invalid registry: %s, optional values is: %s, %s",
                config.getRegistry(), NoneRegistry.REGISTRY, PRPCZooKeeperRegistry.REGISTRY));
    }

    public static Registry createRegistry(PRPConfig.PRegistryCenter registryCenter,
                                          ProtocolType protocolType, int serverPort) {
        String naming = registryCenter.getNaming();
        if (NoneRegistry.REGISTRY.equals(naming)) {
            // TODO
            return new NoneRegistry(null);
        } else if (PRPCZooKeeperRegistry.REGISTRY.equals(naming)) {
            if (protocolType == ProtocolType.prpc) {
                return new PRPCZooKeeperRegistry(serverPort, registryCenter.getAddress(), 30000);
            } else if (protocolType == ProtocolType.dubbo) {
                // TODO
            }
        }

        throw new PRPCException(String.format("invalid registry: %s, optional values is: %s, %s",
                naming, NoneRegistry.REGISTRY, PRPCZooKeeperRegistry.REGISTRY));
    }

    public static Registry createRegistry(PRPConfig.PRegistryCenter registryCenter, List<PRPConfig.PGroup> groups,
                                          ProtocolType protocolType, int serverPort) {
        String naming = registryCenter.getNaming();
        if (NoneRegistry.REGISTRY.equals(naming)) {
            // TODO
            return new NoneRegistry(null);
        } else if (PRPCZooKeeperRegistry.REGISTRY.equals(naming)) {
            if (protocolType == ProtocolType.prpc) {
                return new PRPC2ZooKeeperRegistry(registryCenter, groups, serverPort, 30000);
            } else if (protocolType == ProtocolType.dubbo) {
                // TODO
                return new DubboZooKeeperRegistry(registryCenter, groups, serverPort, 30000);
            }
        }

        throw new PRPCException(String.format("invalid registry: %s, optional values is: %s, %s",
                naming, NoneRegistry.REGISTRY, PRPCZooKeeperRegistry.REGISTRY));
    }
}
