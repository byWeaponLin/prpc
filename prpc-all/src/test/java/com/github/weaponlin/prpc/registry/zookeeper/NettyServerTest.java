package com.github.weaponlin.prpc.registry.zookeeper;

import com.github.weaponlin.prpc.api.HelloApi;
import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.registry.ZooKeeperRegistry;
import com.github.weaponlin.prpc.server.NettyServer;
import com.github.weaponlin.prpc.utils.PortUtils;

public class NettyServerTest {

    public static void main(String[] args) {
        PConfig config = new PConfig();
        config.setRegistry(ZooKeeperRegistry.REGISTRY);
        config.setAddress("127.0.0.1:2181");
        new NettyServer(PortUtils.getAvailablePort(), config)
                .addService(HelloApi.class)
                .start();
    }
}
