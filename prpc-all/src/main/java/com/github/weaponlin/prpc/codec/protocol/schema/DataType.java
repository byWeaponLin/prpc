package com.github.weaponlin.prpc.codec.protocol.schema;


import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Deprecated
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
     * TODO don't support, remove it
     */
    _Object(8, ""),

    /**
     * TODO array
     */
    _Array(9, ""),

    /**
     * TODO List
     */
    _List(10, "java.util.List"),

    _Unknown(11, ""),

    ;

    private int type;

    private String name;

    /**
     * primitive type map
     */
    public static Map<String, DataType> primitiveType;

    /**
     * type name map
     */
    public static Map<String, DataType> typeNameMap;

    /**
     * general type map
     */
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
     * get dataType by type class
     * @param typeClass
     * @return
     */
    public static DataType getDataType(Class<?> typeClass) {
        if (typeClass == null) {
            return DataType._Unknown;
        } else if (generalTypeMap.containsKey(typeClass.getName())) {
            return generalTypeMap.get(typeClass.getName());
        } else if (typeClass.isEnum()) {
            return DataType._Enum;
        } else if (typeClass.isArray()) {
            return DataType._Array;
        }
        // TODO exclude Map, Set, Object
        return Stream.of(typeClass.getInterfaces(), typeClass)
                .filter(i -> i == List.class)
                .findAny()
                .map(i -> DataType._List)
                .orElse(DataType._Object);
    }

}
