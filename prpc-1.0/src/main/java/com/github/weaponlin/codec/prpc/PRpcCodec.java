package com.github.weaponlin.codec.prpc;

import com.github.weaponlin.codec.DataType;
import com.github.weaponlin.codec.PCodec;
import com.github.weaponlin.codec.schema.Schema;

/**
 * key-length-value
 * key: fix one byte, include field number and filed data type
 * length: fix 4 bytes
 * value: value
 *
 * <p>
 *     key-value structure:
 *          byte, Byte, short, Short, int, Integer, float, Float, long, Long, enum
 *     key-length-value structure: length fixed 4 bytes
 *          String, Custom Class
 *     key-size-value: size fixed 4 bytes, max size = 0xFFFF
 *          array, list
 * </p>
 *
 * <p>
 *  support data type: {@link DataType}
 *  not support data type: Map and subClass, Set and subClass
 * </p>
 */
public class PRpcCodec implements PCodec {

    @Override
    public byte[] encode(Object object, Schema schema) {
        // TODO
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes) {
        // TODO
        return null;
    }
}
