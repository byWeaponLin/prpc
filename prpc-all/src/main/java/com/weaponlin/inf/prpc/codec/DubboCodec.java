package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.loader.Extension;

/**
 * TODO
 */
@Extension(name = "dubbo")
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
