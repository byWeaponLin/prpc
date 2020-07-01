package com.github.weaponlin.codec;


import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public enum DataType {

    /**
     * 1 byte
     */
    _byte(1, "byte"),

    /**
     * TODO not primitive type, 1 byte
     */
    _Byte(1, "java.lang.Byte"),

    /**
     * 2 bytes
     */
    _short(2, "short"),

    /**
     * 2 bytes
     */
    _Short(2, "java.lang.Short"),

    /**
     * 4 bytes
     */
    _int(3, "int"),

    /**
     * 4 bytes
     */
    _Integer(3, "java.lang.Integer"),

    /**
     * 8 bytes
     */
    _long(4, "long"),

    /**
     * 8 bytes
     */
    _Long(4, "java.lang.Long"),

    /**
     * 4 bytes
     */
    _float(5, "float"),

    /**
     * 4 bytes
     */
    _Float(5, "java.lang.Float"),

    /**
     * 8 bytes
     */
    _double(6, "double"),

    /**
     * 8 bytes
     */
    _Double(6, "java.lang.Double"),

    /**
     * string
     */
    _String(7, "java.lang.String"),

    /**
     * TODO enum
     */
    _Enum(8, ""),


    /**
     * TODO
     */
    _Object(8, ""),

    /**
     * TODO array
     */
    _array(9, ""),

    /**
     * TODO List
     */
    _List(10, "java.util.List"),

    ;

    private int type;

    private String name;

    /**
     * primitive type
     */
    public static Map<String, DataType> primitiveType;

    public static Map<String, DataType> typeNameMap;

    public static Map<String, DataType> generalTypeMap;

    static {
        primitiveType = ImmutableMap.<String, DataType>builder()
                .put(_byte.name, _byte)
                .put(_short.name, _short)
                .put(_int.name, _int)
                .put(_long.name, _long)
                .put(_float.name, _float)
                .put(_double.name, _double)
                .build();

        typeNameMap = ImmutableMap.<String, DataType>builder()
                .put(_byte.name, _byte)
                .put(_short.name, _short)
                .put(_int.name, _int)
                .put(_long.name, _long)
                .put(_float.name, _float)
                .put(_double.name, _double)
                .put(_String.name, _String)
                .put(_Object.name, _Object)
                .build();

        generalTypeMap = ImmutableMap.<String, DataType>builder()
                .put(_byte.name, _byte)
                .put(_Byte.name, _Byte)
                .put(_short.name, _short)
                .put(_Short.name, _Short)
                .put(_int.name, _int)
                .put(_Integer.name, _Integer)
                .put(_long.name, _long)
                .put(_Long.name, _Long)
                .put(_float.name, _float)
                .put(_Float.name, _Float)
                .put(_double.name, _double)
                .put(_Double.name, _Double)
                .put(_String.name, _String)
                .build();

    }

    /**
     * check an attribute is primitive
     * @param typeName
     * @return
     */
    public static boolean isPrimitiveType(String typeName) {
        return primitiveType.containsKey(typeName);
    }

    public static boolean isGeneralType(String typeName) {
        return generalTypeMap.containsKey(typeName);
    }

    /**
     * get dataType by type name
     * @param typeName
     * @return
     */
    public static DataType getDataType(String typeName) {
        return typeNameMap.getOrDefault(typeName, _Object);
    }

}
