package com.github.weaponlin.prpc.registry.none;

import com.github.weaponlin.prpc.api.HelloApi;
import com.github.weaponlin.prpc.api.HelloRequest;
import com.github.weaponlin.prpc.client.PClient;
import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.registry.NoneRegistry;
import com.github.weaponlin.prpc.registry.ZooKeeperRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NoneRegistryClientTest {

    public static void main(String[] args) {
        PConfig config = new PConfig();
        config.setRegistry(NoneRegistry.REGISTRY);
        config.setAddress("127.0.0.1:50697");
        HelloApi helloApi = new PClient(config).getService(HelloApi.class);

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
            System.out.println("average cost: " + (costs.stream().mapToLong(e -> e).sum() / costs.size()) + ", requests: " + costs.size());
        }
        System.out.println("done");
    }
}