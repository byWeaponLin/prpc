package com.github.weaponlin;

import com.example.grpc.gencode.HelloRequest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws UnsupportedEncodingException {
//        HelloRequest request = HelloRequest.newBuilder()
//                .setFirstName("hello")
//                .setLastName("你好")
//                .build();
//
//        for (byte b : request.toByteArray()) {
//            System.out.print(b + " ");
//        }
//        System.out.println();
//
//        for (byte b : "你".getBytes(StandardCharsets.UTF_8)) {
//            System.out.print(b + " ");
//        }
//        System.out.println();
//        for (byte b : "好".getBytes(StandardCharsets.UTF_8)) {
//            System.out.print(b + " ");
//        }
//
//        byte[] bytes = new byte[]{-27,-91,-67};
//        System.out.println(new String(bytes));
        int a = 0b1101;
        System.out.println(a ^ 1);

    }
}
