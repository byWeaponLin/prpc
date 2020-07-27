package com.github.weaponlin.prpc;

import com.github.weaponlin.prpc.api.HelloApi;
import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.server.NettyServer;
import com.github.weaponlin.prpc.utils.PortUtils;

public class NettyServerTest {

    public static void main(String[] args) {
        PConfig config = new PConfig();
        config.setZookeeper("127.0.0.1:2181");
        new NettyServer(PortUtils.getAvailablePort(), config)
                .addService(HelloApi.class)
                .start();
    }
}
