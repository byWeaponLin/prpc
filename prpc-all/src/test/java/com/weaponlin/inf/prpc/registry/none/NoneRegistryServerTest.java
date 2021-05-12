package com.weaponlin.inf.prpc.registry.none;

import com.weaponlin.inf.prpc.api.HelloApi;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.registry.NoneRegistry;
import com.weaponlin.inf.prpc.server.NettyServer;
import com.weaponlin.inf.prpc.utils.PortUtils;

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
