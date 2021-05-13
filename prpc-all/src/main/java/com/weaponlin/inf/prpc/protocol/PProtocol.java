package com.weaponlin.inf.prpc.protocol;

import com.weaponlin.inf.prpc.protocol.prpc.PPacket;
import io.netty.buffer.ByteBuf;

public interface PProtocol {
    ProtocolType getProtocol();

    String getServiceName();

    String getMethodName();

    void encode(ByteBuf byteBuf, Object msg);

    void decode(ByteBuf byteBuf, Object msg);

    PPacket getPacket(Class<?> clazz);
}
