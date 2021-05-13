package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.loader.ServiceLoader;
import com.weaponlin.inf.prpc.protocol.PProtocol;
import com.weaponlin.inf.prpc.protocol.PProtocolFactory;
import com.weaponlin.inf.prpc.protocol.prpc.PHeader;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
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
            throw new PRpcException("new decoded class instance failed", e);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        PProtocol pprotocol = PProtocolFactory.getProtocol(protocol, codec);
        pprotocol.decode(in, instance);
        out.add(pprotocol.getPacket(decodeClass));

//        PCodec codec = ServiceLoader.getService(PCodec.class, this.codec);
//
//        ByteBuf headByteBuf = in.readBytes(PHeader.HEAD_LEN);
//        PHeader header = PHeader.decode(headByteBuf);
//        header.validate();
//
//        try {
//            byte[] metaBytes = new byte[header.getMetaSize()];
//            in.readBytes(metaBytes);
//            PMeta meta = new PMeta();
//            codec.decode(metaBytes, meta);
//
//            meta.validate();
//
//
//            byte[] bodyBytes = new byte[header.getBodySize()];
//            in.readBytes(bodyBytes);
//            final Object t = decodeClass.newInstance();
//            codec.decode(bodyBytes, t);
//            out.add(t);
//        } catch (Throwable e) {
//            throw new PRpcException("cant deserialize request, class: " + decodeClass.getName(), e);
//        }
    }
}
