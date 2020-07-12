package com.github.weaponlin;

import com.github.weaponlin.api.HelloApi;
import com.github.weaponlin.api.HelloRequest;
import com.github.weaponlin.api.HelloResponse;
import com.github.weaponlin.client.PClientFactory;
import com.github.weaponlin.config.PRPCConfig;

import java.util.Scanner;

public class ClientTest {

    public static void main(String[] args) {
        final PRPCConfig.RegistryProperties registryProperties = new PRPCConfig.RegistryProperties().setHost("127.0.0.1:2181").setTimeout(30000).setGroup("demo").setPath("");
        final HelloApi helloApi = PClientFactory.getService(HelloApi.class, registryProperties);

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
