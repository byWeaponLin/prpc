package com.github.weaponlin.codec;


import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public enum DataType {
    /**
     * 1 byte
     */
    _byte(1),

    /**
     * TODO not primitive type, 1 byte
     */
    _Byte(1),

    /**
     * 2 bytes
     */
    _short(2),

    /**
     * 4 bytes
     */
    _int(3),

    /**
     * 8 bytes
     */
    _long(4),

    /**
     * 4 bytes
     */
    _float(5),

    /**
     * 8 bytes
     */
    _double(6),

    /**
     * string
     */
    _string(7),

    _other(8),

    ;

    private int type;

    private static Map<Integer, DataType> primitiveType = Maps.newHashMap();

    static {
        primitiveType.put(_byte.type, _byte);
        primitiveType.put(_short.type, _short);
        primitiveType.put(_int.type, _int);
        primitiveType.put(_long.type, _long);
        primitiveType.put(_float.type, _float);
        primitiveType.put(_double.type, _double);
    }


    public static boolean isPrimitiveType(int type) {
        return primitiveType.containsKey(type);
    }
}
