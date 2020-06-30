package com.github.weaponlin.codec.schema;

import lombok.Data;

import java.util.List;

@Data
public class Schema {

    private int fieldNumber;

    private String fieldName;

    private Class<?> typeClass;

    private List<Schema> schemas;
}
