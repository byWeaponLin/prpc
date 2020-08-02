package com.github.weaponlin.prpc.registry.none;

import com.github.weaponlin.prpc.api.HelloApi;
import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.registry.NoneRegistry;
import com.github.weaponlin.prpc.server.NettyServer;
import com.github.weaponlin.prpc.utils.PortUtils;

public class NoneRegistryServerTest {
    public static void main(String[] args) {
        PConfig config = new PConfig();
        config.setRegistry(NoneRegistry.REGISTRY);
        final int port = PortUtils.getAvailablePort();
        System.out.println("server port: " + port);
        config.setAddress("127.0.0.1:" + port);
        new NettyServer(port, config)
                .addService(HelloApi.class)
                .start();
    }
}
