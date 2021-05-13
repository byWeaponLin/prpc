package com.weaponlin.inf.prpc.protocol;

import io.netty.buffer.ByteBuf;

public interface Protocol {
    ProtocolType getProtocol();

    String getServiceName();

    String getMethodName();

    void encode(ByteBuf byteBuf, Object msg);

    void decode(ByteBuf byteBuf, Object msg);
}
