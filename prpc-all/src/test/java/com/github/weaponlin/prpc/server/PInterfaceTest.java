package com.github.weaponlin.prpc.server;

import com.github.weaponlin.prpc.api.HelloApi;
import org.junit.Test;

import java.util.stream.Stream;

public class PInterfaceTest {

    @Test
    public void test() {
        Stream.of(HelloApi.class.getDeclaredMethods()).forEach(method -> {
            System.out.println(method.getName());

        });
    }
}