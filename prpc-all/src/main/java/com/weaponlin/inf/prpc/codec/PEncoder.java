package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.loader.ServiceLoader;
import com.weaponlin.inf.prpc.protocol.PProtocolFactory;
import com.weaponlin.inf.prpc.protocol.PProtocol;
import com.weaponlin.inf.prpc.protocol.prpc.PHeader;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.protocol.prpc.PResponse;
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

    private String codec = "protobuf";

    private String protocol = "prpc";

    public PEncoder(@NonNull Class encodeClass) {
        this.encodeClass = encodeClass;
    }

    public PEncoder(Class encodeClass, String codec) {
        this.encodeClass = encodeClass;
        this.codec = codec;
    }

    public PEncoder(Class encodeClass, String codec, String protocol) {
        this.encodeClass = encodeClass;
        this.codec = codec;
        this.protocol = protocol;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // TODO
        PProtocol pprotocol = PProtocolFactory.getProtocol(protocol, codec);
        pprotocol.encode(out, msg);

//        PCodec codec = ServiceLoader.getService(PCodec.class, this.codec);
//        if (PRequest.class == encodeClass) {
//
//            PRequest request = (PRequest) msg;
//            PMeta meta = request.getMeta();
//            byte[] metaBytes = codec.encode(meta);
//            byte[] bodyBytes = codec.encode(request);
//
//            PHeader head = new PHeader();
//            head.setMetaSize(metaBytes.length);
//            head.setBodySize(bodyBytes.length);
//            head.encode(out);
//
//            out.writeBytes(metaBytes);
//            out.writeBytes(bodyBytes);
//
//        } else if (PResponse.class == encodeClass) {
//            PResponse response = (PResponse) msg;
//            PMeta meta = response.getMeta();
//            byte[] metaBytes = codec.encode(meta);
//            byte[] bodyBytes = codec.encode(response);
//
//            PHeader head = new PHeader();
//            head.setMetaSize(metaBytes.length);
//            head.setBodySize(bodyBytes.length);
//            head.encode(out);
//
//            out.writeBytes(metaBytes);
//            out.writeBytes(bodyBytes);
//        }
    }
}
