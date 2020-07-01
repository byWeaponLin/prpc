package com.github.weaponlin.codec.schema;

import com.github.weaponlin.codec.DataType;
import com.github.weaponlin.exception.PException;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        final DataType dataType = DataType.getDataType(typeClass.getTypeName());
        Schema schema = Schema.builder()
                .fieldName(fieldName)
                .fieldNumber(fieldNumber)
                .typeClass(typeClass)
                .dataType(dataType)
                .general(false)
                .build();
        if (DataType.isGeneralType(typeClass.getTypeName())) {
            return schema.setGeneral(true);
        }

        final List<Field> fields = Stream.of(typeClass.getDeclaredFields())
                .filter(field -> (EXCEPT_FIELD_MODIFIER & field.getModifiers()) == 0)
                .collect(toList());

        if (CollectionUtils.isEmpty(fields)) {
            return schema;
        }

        final List<Schema> embeddedSchemas = IntStream.range(0, fields.size()).mapToObj(idx -> {
            final Field field = fields.get(idx);
            return readObjectMeta(field.getName(), field.getType(), idx);
        }).collect(toList());

        schema.setSchemas(embeddedSchemas);
        return schema;
    }
}
