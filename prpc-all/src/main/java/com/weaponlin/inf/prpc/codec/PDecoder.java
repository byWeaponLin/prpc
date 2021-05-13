package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.loader.ServiceLoader;
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
        // TODO 超级慢，急需优化
        PCodec codec = ServiceLoader.getService(PCodec.class, protocolType);

        ByteBuf headByteBuf = in.readBytes(PHeader.HEAD_LEN);
        PHeader header = PHeader.decode(headByteBuf);
        header.validate();

        try {
            byte[] metaBytes = new byte[header.getMetaSize()];
            in.readBytes(metaBytes);
            PMeta meta = new PMeta();
            codec.decode(metaBytes, meta);

            meta.validate();


            byte[] bodyBytes = new byte[header.getBodySize()];
            in.readBytes(bodyBytes);
            final Object t = decodeClass.newInstance();
            codec.decode(bodyBytes, t);
            out.add(t);
        } catch (Throwable e) {
            throw new PRpcException("cant deserialize request, class: " + decodeClass.getName(), e);
        }
    }
}
