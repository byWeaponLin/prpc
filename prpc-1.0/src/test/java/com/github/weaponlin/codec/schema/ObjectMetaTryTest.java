package com.github.weaponlin.codec.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class ObjectMetaTryTest {

    private static final int EXCEPT_FIELD_MODIFIER = Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT | Modifier.VOLATILE;

    @org.junit.Test
    public void get_general_filed() {
        Field[] declaredFields = Test.class.getDeclaredFields();
        List<Field> fields = Stream.of(declaredFields).filter(field -> (EXCEPT_FIELD_MODIFIER & field.getModifiers()) == 0)
                .collect(toList());

        assertEquals(4, fields.size());
        assertEquals("a", fields.get(0).getName());
        assertEquals("b", fields.get(1).getName());
        assertEquals("c", fields.get(2).getName());
        assertEquals("d", fields.get(3).getName());
    }

    @org.junit.Test
    public void test_array() {
        int[] intArray = new int[10];

        Test[] testArray = new Test[10];
        System.out.println(intArray.getClass().getTypeName());
        System.out.println(intArray.getClass().getComponentType().getTypeName());
        System.out.println(testArray.getClass().getTypeName());
        System.out.println(testArray.getClass().getComponentType().getTypeName());

        Object[] objects = new Object[10];
        objects[0] = "11";
        objects[1] = 1;
        objects[2] = 3D;

        assertEquals ("java.lang.String", objects[0].getClass().getTypeName());
        assertEquals ("java.lang.Integer", objects[1].getClass().getTypeName());
        assertEquals ("java.lang.Double", objects[2].getClass().getTypeName());
    }

    @org.junit.Test
    public void test_List() {
        List<Integer> ints = new ArrayList<>();

        ints.add(10);

        System.out.println(ints.getClass().getName());
        System.out.println(ints.getClass().getTypeName());
        System.out.println(ints.getClass().getSuperclass().getTypeName());
        Stream.of(ints.getClass().getInterfaces()).filter(e -> List.class.equals(e)).map(Class::getTypeName).findFirst().ifPresent(System.out::println);
    }

    @org.junit.Test
    public void test_enum() {
        Type type = Type.type1;
        System.out.println(type.ordinal());
        System.out.println(type.getClass().isEnum());
    }

    @Getter
    @AllArgsConstructor
    enum Type {
        type1(1),
        type2(2),
        type3(3)

        ;

        private int type;
    }

    @Data
    private static class Test {
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
    private static class InnerTest {
        private int a;

        private String b;

        private Integer c;
    }
}