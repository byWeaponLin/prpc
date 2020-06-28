package com.github.weaponlin;

import com.github.weaponlin.api.HelloApi;
import com.github.weaponlin.api.HelloRequest;
import com.github.weaponlin.client.PClientFactory;

import java.util.Scanner;

public class ClientTest {

    public static void main(String[] args) {
        final HelloApi helloApi = PClientFactory.getService(HelloApi.class);

        Scanner scanner = new Scanner(System.in);
        int size;
        while ((size = scanner.nextInt()) > 0) {
            System.out.println(helloApi.hello(1L, HelloRequest.builder().size(size).build()));
        }
        System.out.println("done");
    }
}
