package com.weaponlin.inf.prpc.protocol;

import com.weaponlin.inf.prpc.codec.PCodec;
import com.weaponlin.inf.prpc.loader.ServiceLoader;

public abstract class AbstractProtocol implements PProtocol {

    private ProtocolType protocolType;

    private PCodec codec;

    public AbstractProtocol(ProtocolType protocolType, String codec) {
        this.protocolType = protocolType;
        this.codec = ServiceLoader.getService(PCodec.class, codec);
    }

    protected byte[] encode(Object msg) {
        return codec.encode(msg);
    }

    protected void decode(byte[] bytes, Object msg) {
        codec.decode(bytes, msg);
    }
}
