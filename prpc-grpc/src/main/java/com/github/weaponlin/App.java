package com.github.weaponlin;

import com.example.grpc.gencode.HelloRequest;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws UnsupportedEncodingException {
        HelloRequest request = HelloRequest.newBuilder()
                .setFirstName("hello")
                .setLastName("你好")
                .build();

        for (byte b : request.toByteArray()) {
            System.out.print(b + " ");
        }

        System.out.println();

        HelloRequest2 request2 = new HelloRequest2();
        request2.setFirstName("hello");
        request2.setLastName("你好");
        int[] a = {1, 2};
        request2.setArr(a);
        for (byte b : serializer(request2)) {
            System.out.print(b + " ");
        }

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

    }

    public static <T> byte[] serializer(T o) {
        Schema schema = RuntimeSchema.getSchema(o.getClass());
        return ProtobufIOUtil.toByteArray(o, schema, LinkedBuffer.allocate(256));
    }


    public static class HelloRequest2 {
        private String firstName;
        private String lastName;

        private int[] arr;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int[] getArr() {
            return arr;
        }

        public void setArr(int[] arr) {
            this.arr = arr;
        }
    }
}
