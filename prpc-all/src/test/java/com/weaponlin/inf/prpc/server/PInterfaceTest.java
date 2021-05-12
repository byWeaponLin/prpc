package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.api.HelloApi;
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