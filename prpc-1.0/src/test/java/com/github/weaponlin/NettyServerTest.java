package com.github.weaponlin;

import com.github.weaponlin.server.NettyServer;

public class NettyServerTest {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer(8888);
        nettyServer.start();
    }
}
