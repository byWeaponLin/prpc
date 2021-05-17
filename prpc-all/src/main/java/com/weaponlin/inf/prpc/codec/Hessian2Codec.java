package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.loader.Extension;

@Extension(name = "hessian2")
public class Hessian2Codec implements PCodec {
    @Override
    public byte[] encode(Object o) {
        // TODO
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        // TODO
        return null;
    }
}
