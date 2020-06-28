package com.github.weaponlin.codec;

import com.github.weaponlin.codec.schema.Schema;

public class ProtobufCodec implements PCodec {

    @Override
    public byte[] encode(Object object, Schema schema) {
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes) {
        // TODO
        return null;
    }
}
