package com.weaponlin.inf.prpc.registry.prpc.zk_proto;

import com.google.common.collect.Lists;
import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.server.PRPCServer;

public class PRPCServerTest {

    public static void main(String[] args) {
        PRPConfig prpConfig = new PRPConfig();
        PRPConfig.PRegistryCenter registryCenter = new PRPConfig.PRegistryCenter();
        registryCenter.setNaming("zookeeper");
        registryCenter.setAddress("127.0.0.1:2181");
        prpConfig.setRegistryCenter(registryCenter);
        prpConfig.setCodec("protobuf");
        PRPConfig.PGroup group = new PRPConfig.PGroup();
        group.setProtocol("prpc");
        group.setBasePackage("com.weaponlin.inf.prpc.api");
        prpConfig.setGroups(Lists.newArrayList(group));
        new PRPCServer(prpConfig).start();
    }

}