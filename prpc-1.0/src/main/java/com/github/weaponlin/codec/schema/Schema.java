package com.github.weaponlin.codec.schema;

import lombok.Data;

@Data
public class Schema {

    private int fieldNumber;

    private String fieldName;

    private Class<?> typeClass;
}
