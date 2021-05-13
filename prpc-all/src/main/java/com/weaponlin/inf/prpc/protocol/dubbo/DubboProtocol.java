package com.weaponlin.inf.prpc.protocol.dubbo;

import com.weaponlin.inf.prpc.protocol.AbstractProtocol;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import io.netty.buffer.ByteBuf;

/**
 * TODO
 */
public class DubboProtocol extends AbstractProtocol {

    private DubboRequest request;

    private DubboResponse response;

    public DubboProtocol(ProtocolType protocolType, String codec) {
        super(protocolType, codec);
    }

    @Override
    public ProtocolType getProtocol() {
        return ProtocolType.dubbo;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public void encode(ByteBuf byteBuf, Object msg) {

    }

    @Override
    public void decode(ByteBuf byteBuf, Object msg) {

    }
}
