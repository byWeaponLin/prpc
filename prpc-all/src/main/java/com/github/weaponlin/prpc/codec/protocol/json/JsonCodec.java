package com.github.weaponlin.prpc.codec.protocol.json;

import com.github.weaponlin.prpc.codec.protocol.PCodec;

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
