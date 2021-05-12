package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.constants.Constants;
import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.loader.ServiceLoader;
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

    /**
     * protocol type field
     */
    private String protocolType;

    public PDecoder(@NonNull Class decodeClass) {
        this.decodeClass = decodeClass;
    }

    public PDecoder(Class decodeClass, String protocolType) {
        this.decodeClass = decodeClass;
        this.protocolType = protocolType;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        PCodec codec = ServiceLoader.getService(PCodec.class, protocolType);
        int magic = in.readInt();
        if (magic != Constants.MAGIC) {
            throw new PRpcException("invalid protocol");
        }
        final int bytesLen = in.readableBytes();
        byte[] bytes = new byte[bytesLen];
        in.readBytes(bytes);
        // TODO 太消耗性能，待优化
        final Object t = decodeClass.newInstance();
        codec.decode(bytes, t);
        out.add(t);
    }
}
