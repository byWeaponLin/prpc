package com.weaponlin.inf.prpc.registry.prpc.zk_jackson;

import com.google.common.collect.Lists;
import com.weaponlin.inf.prpc.api.hello.HelloApi;
import com.weaponlin.inf.prpc.api.hello.HelloRequest;
import com.weaponlin.inf.prpc.client.PRPClient;
import com.weaponlin.inf.prpc.config.PRPConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientTest {

    public static void main(String[] args) {
        PRPConfig config = new PRPConfig();
        PRPConfig.PRegistryCenter registryCenter = new PRPConfig.PRegistryCenter();
        registryCenter.setNaming("zookeeper");
        registryCenter.setAddress("127.0.0.1:2181");
        config.setRegistryCenter(registryCenter);
        config.setLoadBalance("roundrobin");
        PRPConfig.PGroup group = new PRPConfig.PGroup();
        group.setProtocol("prpc");
        group.setBasePackage("com.weaponlin.inf.prpc.api");
        group.setLoadBalance("roundrobin");
        group.setCodec("jackson");
        config.setGroups(Lists.newArrayList(group));
        HelloApi helloApi = new PRPClient(config).getService(HelloApi.class);

        Scanner scanner = new Scanner(System.in);
        // TODO if size = 2222222 then client get response is null
        int times;
        Random random = new Random();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 30, TimeUnit.MINUTES, new LinkedBlockingDeque<>());
        while ((times = scanner.nextInt()) > 0) {
            final List<Long> costs = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < times; i++) {
                final Future<Long> future = executor.submit(() -> {
                    final int length = Math.abs(random.nextInt(100));
                    long start = System.currentTimeMillis();
                    helloApi.hello(1L, HelloRequest.builder().size(length).build());
                    final long end = System.currentTimeMillis();
                    System.out.println("cost: " + (end - start));
                    return end - start;
                });
                try {
                    final Long cost = future.get();
                    costs.add(cost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println("average cost: " + (costs.stream().mapToLong(e -> e).sum() / costs.size()) + ", requests: " + costs.size());
            } catch (Exception e) {

            }
        }
        System.out.println("done");
    }
}
