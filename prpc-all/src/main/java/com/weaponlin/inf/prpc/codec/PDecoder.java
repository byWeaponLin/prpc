package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.PProtocol;
import com.weaponlin.inf.prpc.protocol.PProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * decoder
 */
@Slf4j
public class PDecoder extends ByteToMessageDecoder {

    private Class decodeClass;

    private String codec = "protobuf";

    private String protocol = "prpc";

    private Object instance;

    public PDecoder(@NonNull Class decodeClass) {
        this(decodeClass, "protobuf");
    }

    public PDecoder(Class decodeClass, String codec) {
        this(decodeClass, codec, "prpc");
    }

    public PDecoder(Class decodeClass, String codec, String protocol) {
        this.decodeClass = decodeClass;
        this.codec = codec;
        this.protocol = protocol;
        try {
            instance = decodeClass.newInstance();
        } catch (Exception e) {
            throw new PRPCException("new decoded class instance failed", e);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        PProtocol pprotocol = PProtocolFactory.getProtocol(protocol, codec);
        pprotocol.decode(in, instance);
        out.add(pprotocol.getPacket(decodeClass));
    }
}
