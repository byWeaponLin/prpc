package com.github.weaponlin.codec.schema;

import com.github.weaponlin.codec.DataType;
import com.github.weaponlin.exception.PException;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.weaponlin.codec.DataType.*;
import static java.util.stream.Collectors.toList;

/**
 * TODO
 */
public class ObjectMeta {

    public static final int EXCEPT_FIELD_MODIFIER = Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT | Modifier.VOLATILE;


    public static Schema readObjectMeta(String fieldName, Class<?> typeClass, int fieldNumber) {

        if (typeClass == null) {
            throw new PException("typeClass is null");
        }

        final DataType dataType = getDataType(typeClass);
        Schema schema = Schema.builder()
                .fieldName(fieldName)
                .fieldNumber(fieldNumber)
                .typeClass(typeClass)
                .dataType(dataType)
                .general(false)
                .build();
        switch (dataType) {
            case _byte:
            case _Byte:
            case _short:
            case _Short:
            case _int:
            case _Integer:
            case _long:
            case _Long:
            case _float:
            case _Float:
            case _double:
            case _Double:
            case _String:
            case _Enum:
                schema.setGeneral(true);
                return schema;
            case _Object:
                schema.setGeneral(false);
                schema.setSchemas(readEmbeddedSchema(typeClass));
                return schema;
            case _Array:
            case _List:
                schema.setGeneral(false);
                return schema;
            case _Unknown:
                schema.setGeneral(false);
                return schema;
            default:
                schema.setGeneral(false);
                return schema;
        }
    }

    /**
     * read embedded schema
     * @param typeClass
     * @return
     */
    private static List<Schema> readEmbeddedSchema(Class<?> typeClass) {
        // read object class field info
        final List<Field> fields = Stream.of(typeClass.getDeclaredFields())
                .filter(field -> (EXCEPT_FIELD_MODIFIER & field.getModifiers()) == 0)
                .collect(toList());

        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        return IntStream.range(0, fields.size()).mapToObj(idx -> {
            final Field field = fields.get(idx);
            return readObjectMeta(field.getName(), field.getType(), idx);
        }).collect(toList());
    }
}
