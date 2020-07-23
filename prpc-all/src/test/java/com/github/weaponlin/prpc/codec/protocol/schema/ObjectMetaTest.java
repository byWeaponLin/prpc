package com.github.weaponlin.prpc.codec.protocol.schema;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectMetaTest {


    @Test
    public void test_int() {
        final Schema schema = ObjectMeta.readObjectMeta("a", int.class, 0);

        assertEquals("a", schema.getFieldName());
        assertEquals(DataType._int, schema.getDataType());
        assertTrue(schema.isGeneral());
        assertTrue(CollectionUtils.isEmpty(schema.getSchemas()));
        assertEquals(0, schema.getFieldNumber());
        assertEquals(int.class, schema.getTypeClass());
    }

    @Test
    public void test_object() {

        @Data
        class Test1 {
            private byte d;

            private String e;

            private int[] f;

            private String[] g;

            private List h;

            private List<Integer> i;
        }

        @Data
        class Test {
            private int a;

            private String b;

            private Test1 c;
        }

        final Schema schema = ObjectMeta.readObjectMeta("test", Test.class, 0);

        assertEquals("test", schema.getFieldName());
        assertEquals(DataType._Object, schema.getDataType());
        assertFalse(schema.isGeneral());
        assertTrue(CollectionUtils.isNotEmpty(schema.getSchemas()));
        assertEquals(0, schema.getFieldNumber());
        assertEquals(Test.class, schema.getTypeClass());

        final List<Schema> fields = schema.getSchemas();

        assertEquals("a", fields.get(0).getFieldName());
        assertEquals(DataType._int, fields.get(0).getDataType());
        assertTrue(fields.get(0).isGeneral());
        assertTrue(CollectionUtils.isEmpty(fields.get(0).getSchemas()));
        assertEquals(0, fields.get(0).getFieldNumber());
        assertEquals(int.class, fields.get(0).getTypeClass());

        assertEquals("b", fields.get(1).getFieldName());
        assertEquals(DataType._String, fields.get(1).getDataType());
        assertTrue(fields.get(1).isGeneral());
        assertTrue(CollectionUtils.isEmpty(fields.get(1).getSchemas()));
        assertEquals(1, fields.get(1).getFieldNumber());
        assertEquals(String.class, fields.get(1).getTypeClass());

        assertEquals("c", fields.get(2).getFieldName());
        assertEquals(DataType._Object, fields.get(2).getDataType());
        assertFalse(fields.get(2).isGeneral());
        assertTrue(CollectionUtils.isNotEmpty(fields.get(2).getSchemas()));
        assertEquals(2, fields.get(2).getFieldNumber());
        assertEquals(Test1.class, fields.get(2).getTypeClass());

        final List<Schema> embeddedFields = fields.get(2).getSchemas();

        assertEquals("d", embeddedFields.get(0).getFieldName());
        assertEquals(DataType._byte, embeddedFields.get(0).getDataType());
        assertTrue(embeddedFields.get(0).isGeneral());
        assertTrue(CollectionUtils.isEmpty(embeddedFields.get(0).getSchemas()));
        assertEquals(0, embeddedFields.get(0).getFieldNumber());
        assertEquals(byte.class, embeddedFields.get(0).getTypeClass());

        assertEquals("e", embeddedFields.get(1).getFieldName());
        assertEquals(DataType._String, embeddedFields.get(1).getDataType());
        assertTrue(embeddedFields.get(1).isGeneral());
        assertTrue(CollectionUtils.isEmpty(embeddedFields.get(1).getSchemas()));
        assertEquals(1, embeddedFields.get(1).getFieldNumber());
        assertEquals(String.class, embeddedFields.get(1).getTypeClass());

        assertEquals("f", embeddedFields.get(2).getFieldName());
        assertEquals(DataType._Array, embeddedFields.get(2).getDataType());
        assertFalse(embeddedFields.get(2).isGeneral());
        assertTrue(CollectionUtils.isEmpty(embeddedFields.get(2).getSchemas()));
        assertEquals(2, embeddedFields.get(2).getFieldNumber());
//        assertEquals("", embeddedFields.get(2).getTypeClass());

        assertEquals("g", embeddedFields.get(3).getFieldName());
        assertEquals(DataType._Array, embeddedFields.get(3).getDataType());
        assertFalse(embeddedFields.get(3).isGeneral());
        assertTrue(CollectionUtils.isEmpty(embeddedFields.get(3).getSchemas()));
        assertEquals(3, embeddedFields.get(3).getFieldNumber());
//        assertEquals(String.class, embeddedFields.get(3).getTypeClass());

        assertEquals("h", embeddedFields.get(4).getFieldName());
        assertEquals(DataType._List, embeddedFields.get(4).getDataType());
        assertFalse(embeddedFields.get(4).isGeneral());
        assertTrue(CollectionUtils.isEmpty(embeddedFields.get(4).getSchemas()));
        assertEquals(4, embeddedFields.get(4).getFieldNumber());
//        assertEquals(String.class, embeddedFields.get(4).getTypeClass());

        assertEquals("i", embeddedFields.get(5).getFieldName());
        assertEquals(DataType._List, embeddedFields.get(5).getDataType());
        assertFalse(embeddedFields.get(5).isGeneral());
        assertTrue(CollectionUtils.isEmpty(embeddedFields.get(5).getSchemas()));
        assertEquals(5, embeddedFields.get(5).getFieldNumber());
//        assertEquals(String.class, embeddedFields.get(5).getTypeClass());
    }
}