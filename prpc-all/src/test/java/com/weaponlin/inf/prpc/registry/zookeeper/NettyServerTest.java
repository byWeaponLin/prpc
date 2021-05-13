package com.weaponlin.inf.prpc.registry.zookeeper;

import com.weaponlin.inf.prpc.api.HelloApi;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.registry.PRPCZooKeeperRegistry;
import com.weaponlin.inf.prpc.server.NettyServer;
import com.weaponlin.inf.prpc.utils.PortUtils;

public class NettyServerTest {

    public static void main(String[] args) {
        PConfig config = new PConfig();
        config.setRegistry(PRPCZooKeeperRegistry.REGISTRY);
        config.setAddress("127.0.0.1:2181");
        new NettyServer(PortUtils.getAvailablePort(), config)
                .addService(HelloApi.class)
                .start();
    }
}
