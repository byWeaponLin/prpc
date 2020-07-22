package com.github.weaponlin;

import com.github.weaponlin.api.HelloApi;
import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.server.NettyServer;
import com.github.weaponlin.utils.PortUtils;
import com.google.common.collect.Lists;

public class NettyServerTest {

    public static void main(String[] args) {
        final PRPCConfig.RegistryProperties registryProperties = new PRPCConfig.RegistryProperties().setHost("127.0.0.1:2181").setTimeout(3000).setGroup("demo").setPath("");
        NettyServer nettyServer = new NettyServer(PortUtils.getAvailablePort(), registryProperties, Lists.newArrayList(HelloApi.class));
        nettyServer.start();
    }
}
