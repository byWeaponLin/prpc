package com.github.weaponlin.prpc;

import com.github.weaponlin.prpc.api.HelloApi;
import com.github.weaponlin.prpc.api.HelloRequest;
import com.github.weaponlin.prpc.client.PClient;
import com.github.weaponlin.prpc.config.PRPCConfig;
import com.google.common.collect.Lists;

import java.util.Scanner;

public class ClientTest {

    public static void main(String[] args) {
        final PRPCConfig.RegistryProperties registryProperties = new PRPCConfig.RegistryProperties().
                setHost("127.0.0.1:2181").setTimeout(30000)
                .setGroup("demo").setPath("");
        PClient client = PClient.builder()
                .codec("protobuf")
                .loadBalance("random")
                .failStrategy("failfast")
                .discovery(registryProperties)
                .services(Lists.newArrayList(HelloApi.class))
                .build()
                .ready();
        HelloApi helloApi = client.getService(HelloApi.class);

        Scanner scanner = new Scanner(System.in);
        // TODO if size = 2222222 then client get response is null
        int size;
        while ((size = scanner.nextInt()) > 0) {
            long start = System.currentTimeMillis();
            helloApi.hello(1L, HelloRequest.builder().size(size).build());
            System.out.println("cost: " + (System.currentTimeMillis() - start));
        }
        System.out.println("done");
    }
}
