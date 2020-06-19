package com.github.weaponlin;

import com.github.weaponlin.api.HelloApi;
import com.github.weaponlin.api.HelloApiImpl;
import com.github.weaponlin.api.HelloRequest;
import com.github.weaponlin.inf.PRPCProxy;

import java.lang.reflect.Proxy;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        HelloApi target = new HelloApiImpl();

        HelloApi proxy = (HelloApi) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), new PRPCProxy(target));

        Scanner scanner = new Scanner(System.in);

        int size;
        while ((size = scanner.nextInt()) > 0) {
            System.out.println(proxy.hello(1L, HelloRequest.builder().size(size).build()));
        }
        System.out.println("done");
    }
}
