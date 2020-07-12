package com.github.weaponlin;

import com.github.weaponlin.api.HelloApi;
import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.server.NettyServer;
import com.google.common.collect.Lists;

public class NettyServerTest {

    public static void main(String[] args) {
        final PRPCConfig.RegistryProperties registryProperties = new PRPCConfig.RegistryProperties().setHost("127.0.0.1:2181").setTimeout(30000).setGroup("demo").setPath("");
        NettyServer nettyServer = new NettyServer(8888, registryProperties, Lists.newArrayList(HelloApi.class));
        nettyServer.start();
    }
}
