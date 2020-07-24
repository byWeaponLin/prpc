package com.github.weaponlin.prpc.codec;

import com.github.weaponlin.prpc.codec.protocol.PCodec;
import com.github.weaponlin.prpc.loader.ServiceLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * prpc encoder
 */
@Slf4j
@Sharable
public class PEncoder extends MessageToByteEncoder {

    private Class encodeClass;

    private String protocolType;

    public PEncoder(@NonNull Class encodeClass) {
        this.encodeClass = encodeClass;
    }

    public PEncoder(Class encodeClass, String protocolType) {
        this.encodeClass = encodeClass;
        this.protocolType = protocolType;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        PCodec codec = ServiceLoader.getService(PCodec.class, protocolType);
        final byte[] encodedBytes = codec.encode(msg);
        out.writeBytes(encodedBytes);
    }
}
