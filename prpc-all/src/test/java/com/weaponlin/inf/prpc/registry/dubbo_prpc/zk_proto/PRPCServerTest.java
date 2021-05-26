package com.weaponlin.inf.prpc.registry.dubbo_prpc.zk_proto;

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
        PRPConfig.PGroup group1 = new PRPConfig.PGroup();
        group1.setProtocol("prpc");
        group1.setBasePackage("com.weaponlin.inf.prpc.api.hello");

        PRPConfig.PGroup group2 = new PRPConfig.PGroup();
        group2.setProtocol("dubbo");
        group2.setBasePackage("com.weaponlin.inf.prpc.api.echo");
        group2.setCodec("hessian2");

        prpConfig.setGroups(Lists.newArrayList(group1, group2));
        new PRPCServer(prpConfig).start();
    }

}