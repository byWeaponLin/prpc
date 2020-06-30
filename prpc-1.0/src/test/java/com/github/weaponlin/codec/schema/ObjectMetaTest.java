package com.github.weaponlin.codec.schema;

import lombok.Data;

import java.lang.reflect.Field;

public class ObjectMetaTest {

    public static void main(String[] args) {
        getSchema(Test.class);
    }

    public static void getSchema(Class<?> clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            System.out.println(field.getName() + ", " + field.getType().getName());
        }
    }

    @Data
    public static class Test {
        private int a;

        private String b;

        private Integer c;

        private InnerTest d;

        private static int e;

        private final float f;

        private static final String g = "g";

        private volatile Long h = 1L;

        private transient float i = 1f;


    }

    @Data
    public static class InnerTest {
        private int a;

        private String b;

        private Integer c;
    }
}