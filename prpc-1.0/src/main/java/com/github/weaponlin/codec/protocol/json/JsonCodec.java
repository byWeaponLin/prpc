package com.github.weaponlin.codec.protocol.json;

import com.github.weaponlin.codec.protocol.PCodec;
import com.github.weaponlin.codec.protocol.schema.Schema;

/**
 * TODO
 */
public class JsonCodec implements PCodec {


    @Override
    public byte[] encode(Object o) {
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        return null;
    }
}
