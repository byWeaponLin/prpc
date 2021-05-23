package com.weaponlin.inf.prpc.registry;

import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.registry.none.NoneRegistry;
import com.weaponlin.inf.prpc.registry.zookeeper.DubboZooKeeperRegistry;
import com.weaponlin.inf.prpc.registry.zookeeper.PRPCZooKeeperRegistry;

import java.util.List;

public class RegistryFactory {

    public static Registry createRegistry(PRPConfig.PRegistryCenter registryCenter,
                                          List<PRPConfig.PGroup> groups,
                                          ProtocolType protocolType,
                                          int serverPort) {
        String naming = registryCenter.getNaming();
        if (NoneRegistry.REGISTRY.equals(naming)) {
            return new NoneRegistry();
        } else if (PRPCZooKeeperRegistry.REGISTRY.equals(naming)) {
            if (protocolType == ProtocolType.prpc) {
                return new PRPCZooKeeperRegistry(registryCenter, groups, serverPort, 30000);
            } else if (protocolType == ProtocolType.dubbo) {
                // TODO
                return new DubboZooKeeperRegistry(registryCenter, groups, serverPort, 30000);
            }
        }

        throw new PRPCException(String.format("invalid registry: %s, optional values is: %s, %s",
                naming, NoneRegistry.REGISTRY, PRPCZooKeeperRegistry.REGISTRY));
    }
}
