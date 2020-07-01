package com.github.weaponlin.codec.prpc;

import com.github.weaponlin.codec.DataType;
import com.github.weaponlin.codec.PCodec;
import com.github.weaponlin.codec.schema.Schema;

/**
 * key-length-value
 * key: fix one byte, include field number and filed data type
 * length: fix 4 byte
 * value: value
 * support data type {@link DataType}
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
