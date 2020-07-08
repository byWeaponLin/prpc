package com.github.weaponlin.codec.protocol.schema;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Deprecated
@Data
@Builder
@Accessors(chain = true)
public class Schema {

    private int fieldNumber;

    private String fieldName;

    private Class<?> typeClass;

    private List<Schema> schemas;

    private DataType dataType;

    /**
     * TODO
     * true if data type among {@link DataType._byte....}
     */
    private boolean general;
}
