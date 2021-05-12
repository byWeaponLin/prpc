package com.github.weaponlin.prpc.codec.protocol.prpc;

import com.github.weaponlin.prpc.codec.protocol.PCodec;
import com.github.weaponlin.prpc.loader.Extension;

/**
 * TODO
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
 *     key-capacity-value: capacity fixed 4 bytes, max capacity = 0xFFFF
 *          array, list
 * </p>
 *
 * <p>
 *  not support data type: Map and subClass, Set and subClass
 * </p>
 */
@Extension(name = "prpc")
public class PRpcCodec implements PCodec {

    @Override
    public byte[] encode(Object o) {
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        return null;
    }
}
