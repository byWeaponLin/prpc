package com.github.weaponlin.prpc.codec.protocol.dubbo;

import com.github.weaponlin.prpc.codec.protocol.PCodec;

/**
 * TODO
 */
public class DubboCodec implements PCodec {


    @Override
    public byte[] encode(Object o) {
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        return null;
    }
}
